package stud.ntnu.backend.service;

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
import stud.ntnu.backend.dto.auth.RegisterRequestDto;
import stud.ntnu.backend.model.user.EmailToken;
import stud.ntnu.backend.model.user.Role;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.repository.EmailTokenRepository;
import stud.ntnu.backend.repository.UserRepository;
import stud.ntnu.backend.util.JwtUtil;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for handling authentication-related operations.
 */
@Service
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final EmailService emailService;
  private final EmailTokenRepository emailTokenRepository;
  private static final Logger log = LoggerFactory.getLogger(AuthService.class);

  public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
      UserRepository userRepository, PasswordEncoder passwordEncoder,
      EmailService emailService,
      EmailTokenRepository emailTokenRepository) {
    this.authenticationManager = authenticationManager;
    this.jwtUtil = jwtUtil;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.emailService = emailService;
    this.emailTokenRepository = emailTokenRepository;
  }

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
        new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
    );

    // Set the authentication in the security context
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // Generate JWT token
    String jwt = jwtUtil.generateToken(authRequest.getEmail());

    // Get user details to include in the response
    User user = userRepository.findByEmail(authRequest.getEmail())
        .orElseThrow(() -> new BadCredentialsException("User not found"));

    // Create and return the response
    return new AuthResponseDto(
        jwt,
        user.getId(),
        user.getEmail(),
        user.getRole().getName(),
        user.getHousehold() != null ? user.getHousehold().getId() : null
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
  public void register(RegisterRequestDto registrationRequest) {
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

    // Save the user
    User savedUser = userRepository.save(newUser);

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
   * @throws IllegalStateException if the token is expired or already used.
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

    log.info("Email verified successfully for user: {}", user.getEmail());
  }
}
