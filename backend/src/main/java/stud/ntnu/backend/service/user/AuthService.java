package stud.ntnu.backend.service.user;

import jakarta.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.backend.dto.auth.AuthRequestDto;
import stud.ntnu.backend.dto.auth.AuthResponseDto;
import stud.ntnu.backend.dto.auth.ChangePasswordDto;
import stud.ntnu.backend.dto.auth.RegisterRequestDto;
import stud.ntnu.backend.model.user.EmailToken;
import stud.ntnu.backend.model.user.Role;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.repository.user.EmailTokenRepository;
import stud.ntnu.backend.repository.user.UserRepository;
import stud.ntnu.backend.util.JwtUtil;
import java.util.Optional;
import stud.ntnu.backend.model.user.Notification;
import stud.ntnu.backend.model.user.NotificationPreference;
import stud.ntnu.backend.repository.user.NotificationPreferenceRepository;
import stud.ntnu.backend.validation.PasswordValidator;
import lombok.RequiredArgsConstructor;


/**
 * Service for handling authentication-related operations.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final EmailService emailService;
  private final EmailTokenRepository emailTokenRepository;
  private final TwoFactorCodeService twoFactorCodeService;
  private final NotificationPreferenceRepository notificationPreferenceRepository;

  /**
   * Authenticate a user and generate a JWT token.
   *
   * @param authRequest the authentication request containing email and password
   * @return an AuthResponseDto containing the JWT token and user information
   * @throws BadCredentialsException if authentication fails
   */
  public AuthResponseDto login(AuthRequestDto authRequest) {
    // Authenticate the user
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(authRequest.getEmail(),
            authRequest.getPassword())
    );

    // Set the authentication in the security context
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // Get user details to include in the response
    User user = userRepository.findByEmail(authRequest.getEmail())
        .orElseThrow(() -> new BadCredentialsException("User not found"));

    // Check if 2FA is enabled
    if (user.getIsUsing2FA()) {
      // Return a response indicating 2FA is required
      return new AuthResponseDto(null, user.getId(), user.getEmail(),
          user.getRole().getName(),
          user.getHousehold() != null ? user.getHousehold().getId() : null, true);
    }

    // Generate JWT token
    String jwt = jwtUtil.generateToken(authRequest.getEmail());

    // Create and return the response
    return new AuthResponseDto(
        jwt,
        user.getId(),
        user.getEmail(),
        user.getRole().getName(),
        user.getHousehold() != null ? user.getHousehold().getId() : null,
        false
    );
  }

  /**
   * Register a new user with the USER role.
   *
   * @param registrationRequest the registration request containing email, password, name, and
   *                            optional home address and location coordinates
   * @throws IllegalArgumentException if a user with the given email already exists
   */
  @Transactional
  public void register(RegisterRequestDto registrationRequest) throws MessagingException
  {
    // Check if user already exists
    if (userRepository.existsByEmail(registrationRequest.getEmail())) {
      throw new IllegalArgumentException("User with this email already exists");
    }

    // Create a new user with USER role (ID 1)
    Role userRole = new Role();
    userRole.setId(1);

    // Create the user with hashed password
    User newUser = new User(
        registrationRequest.getEmail(),
        passwordEncoder.encode(registrationRequest.getPassword()),
        registrationRequest.getPhoneNumber(),
        userRole
    );

    // Set additional fields
    newUser.setFirstName(registrationRequest.getFirstName());
    newUser.setLastName(registrationRequest.getLastName());
    newUser.setHomeAddress(registrationRequest.getHomeAddress());
    newUser.setHomeLatitude(registrationRequest.getHomeLatitude());
    newUser.setHomeLongitude(registrationRequest.getHomeLongitude());
    newUser.setPrivacyAccepted(registrationRequest.getPrivacyPolicyAccepted());

    // Save the user
    User savedUser = userRepository.save(newUser);

    // Create notification preferences for all types
    for (Notification.PreferenceType preferenceType : Notification.PreferenceType.values()) {
      NotificationPreference preference = new NotificationPreference(savedUser, preferenceType);
      notificationPreferenceRepository.save(preference);
    }

    // Generate verification token
    String token = UUID.randomUUID().toString();
    LocalDateTime expiresAt = LocalDateTime.now().plusHours(24);

    // Create and save the EmailToken
    EmailToken verificationToken = new EmailToken(
        savedUser,
        token,
        EmailToken.TokenType.VERIFICATION,
        expiresAt
    );
    emailTokenRepository.save(verificationToken);

    // Send the verification email
    emailService.sendVerificationEmail(savedUser, token);
  }

  /**
   * Verifies a user's email address using the provided token.
   *
   * @param token The verification token string.
   * @throws IllegalArgumentException if the token is invalid or not found.
   * @throws IllegalStateException    if the token is expired or already used.
   */
  @Transactional
  public void verifyEmail(String token) {
    // 1. Find the token
    Optional<EmailToken> tokenOptional = emailTokenRepository.findByToken(token);
    if (tokenOptional.isEmpty()) {
      throw new IllegalArgumentException("Invalid verification token");
    }
    EmailToken emailToken = tokenOptional.get();

    // 2. Validate the token
    if (emailToken.getUsedAt() != null) {
      throw new IllegalStateException("Token has already been used");
    }
    if (emailToken.getExpiresAt().isBefore(LocalDateTime.now())) {
      throw new IllegalStateException("Token has expired");
    }
    if (emailToken.getType() != EmailToken.TokenType.VERIFICATION) {
      throw new IllegalStateException("Invalid token type");
    }

    // 3. Activate the user
    User user = emailToken.getUser();
    if (user == null) {
      throw new IllegalStateException("Token is not associated with a user");
    }
    user.setEmailVerified(true);
    userRepository.save(user);

    // 4. Mark token as used
    emailToken.setUsedAt(LocalDateTime.now());
    emailTokenRepository.save(emailToken);
  }

  public AuthResponseDto verify2FA(String email, Integer code) {
    // Verify 2FA code
    if (!twoFactorCodeService.verifyCode(email, code)) {
      throw new IllegalArgumentException("Invalid 2FA code");
    }

    // Generate JWT token
    String jwt = jwtUtil.generateToken(email);

    // Retrieve user details
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    // Create and return the response
    return new AuthResponseDto(
        jwt,
        user.getId(),
        user.getEmail(),
        user.getRole().getName(),
        user.getHousehold() != null ? user.getHousehold().getId() : null,
        false
    );
  }


  public void send2FACode(String email) throws MessagingException {
    twoFactorCodeService.sendVerificationCode(email);
  }

  /**
   * Initiates the password reset process for a user. Generates a reset token, saves it, and sends a
   * password reset email.
   *
   * @param email The email address of the user requesting a password reset
   * @throws IllegalArgumentException if no user is found with the given email
   */
  @Transactional
  public void forgotPassword(String email) {
    // Find the user by email
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("No user found with email: " + email));

    // Generate reset token
    String token = UUID.randomUUID().toString();
    LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10);

    // Create and save the EmailToken
    EmailToken resetToken = new EmailToken(
        user,
        token,
        EmailToken.TokenType.RESET,
        expiresAt
    );
    emailTokenRepository.save(resetToken);

    // Send the password reset email
    emailService.sendPasswordResetEmail(user, token);
  }

  /**
   * Resets a user's password using a reset token.
   *
   * @param token       The reset token
   * @param newPassword The new password
   * @throws IllegalArgumentException if the token is invalid or not found
   * @throws IllegalStateException    if the token is expired or already used
   */
  @Transactional
  public void resetPassword(String token, String newPassword) {
    // 1. Find the token
    Optional<EmailToken> tokenOptional = emailTokenRepository.findByToken(token);
    if (tokenOptional.isEmpty()) {
      throw new IllegalArgumentException("Invalid reset token");
    }
    EmailToken emailToken = tokenOptional.get();

    // 2. Validate the token
    if (emailToken.getUsedAt() != null) {
      throw new IllegalStateException("Token has already been used");
    }
    if (emailToken.getExpiresAt().isBefore(LocalDateTime.now())) {
      throw new IllegalStateException("Token has expired");
    }
    if (emailToken.getType() != EmailToken.TokenType.RESET) {
      throw new IllegalStateException("Invalid token type");
    }

    // 3. Update the user's password
    User user = emailToken.getUser();
    if (user == null) {
      throw new IllegalStateException("Token is not associated with a user");
    }
    user.setPasswordHash(passwordEncoder.encode(newPassword));
    userRepository.save(user);

    // 4. Mark token as used
    emailToken.setUsedAt(LocalDateTime.now());
    emailTokenRepository.save(emailToken);
  }

  /**
   * Changes the password for the currently authenticated user.
   *
   * @param changePasswordDto DTO containing the old and new passwords
   * @throws IllegalArgumentException if the old password does not match
   */
  @Transactional
  public void changePassword(ChangePasswordDto changePasswordDto) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("No user found with email: " + email));

    if (!passwordEncoder.matches(changePasswordDto.getOldPassword(), user.getPasswordHash())) {
      throw new IllegalArgumentException("Failed to authenticate user");
    }

    PasswordValidator.validate(changePasswordDto.getOldPassword(),
        changePasswordDto.getNewPassword(),
        changePasswordDto.getConfirmNewPassword());

    user.setPasswordHash(passwordEncoder.encode(changePasswordDto.getNewPassword()));
    userRepository.save(user);
  }
}
