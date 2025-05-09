package stud.ntnu.backend.service.user;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import stud.ntnu.backend.repository.user.UserRepository;
import stud.ntnu.backend.repository.user.EmailTokenRepository;
import stud.ntnu.backend.repository.user.SafetyConfirmationRepository;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.model.user.EmailToken;
import stud.ntnu.backend.model.user.SafetyConfirmation;
import stud.ntnu.backend.dto.user.UserProfileDto;
import stud.ntnu.backend.dto.user.UserUpdateDto;
import stud.ntnu.backend.dto.user.UserPreferencesDto;
import stud.ntnu.backend.dto.user.UserHistoryDto;
import stud.ntnu.backend.dto.user.UserHistoryDto.GamificationActivityDto;
import stud.ntnu.backend.dto.user.UserHistoryDto.ReflectionDto;
import stud.ntnu.backend.dto.user.UserBasicInfoDto;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import stud.ntnu.backend.security.AdminChecker;

/**
 * Service for managing users. Handles retrieval, updating, and deletion of users. Note: User
 * creation is handled by AuthService.
 */
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final EmailTokenRepository emailTokenRepository;
  private final SafetyConfirmationRepository safetyConfirmationRepository;
  private final EmailService emailService;
  private final NotificationService notificationService;

  /**
   * Retrieves all users.
   *
   * @return list of all users
   */
  public List<User> getAllUsers()
  {
    return userRepository.findAll();
  }

  /**
   * Retrieves a user by their ID.
   *
   * @param id the ID of the user
   * @return an Optional containing the user if found
   */
  public Optional<User> getUserById(Integer id)
  {
    return userRepository.findById(id);
  }

  /**
   * Retrieves a user by their email.
   *
   * @param email the email of the user
   * @return an Optional containing the user if found
   */
  public Optional<User> getUserByEmail(String email)
  {
    return userRepository.findByEmail(email);
  }

  public Integer getUserIdByEmail(String email)
  {
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("User not found"))
        .getId();
  }

  /**
   * Saves a user.
   *
   * @param user the user to save
   * @return the saved user
   */
  public User saveUser(User user)
  {
    return userRepository.save(user);
  }

  /**
   * Deletes a user by their ID.
   *
   * @param id the ID of the user to delete
   */
  public void deleteUser(Integer id)
  {
    userRepository.deleteById(id);
  }

  /**
   * Gets the profile of a user by their email.
   *
   * @param email the email of the user
   * @return the user's profile
   * @throws IllegalStateException if the user is not found
   */
  public UserProfileDto getUserProfile(String email)
  {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("User not found"));

    return convertToUserProfileDto(user);
  }

  /**
   * Updates a user's profile.
   *
   * @param email         the email of the user
   * @param userUpdateDto the user information to update
   * @return the updated user profile
   * @throws IllegalStateException if the user is not found
   */
  public UserProfileDto updateUserProfile(String email, UserUpdateDto userUpdateDto)
  {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("User not found"));

    // Update user fields if provided
    if (userUpdateDto.getFirstName() != null)
    {
      user.setFirstName(userUpdateDto.getFirstName());
    }

    if (userUpdateDto.getLastName() != null)
    {
      user.setLastName(userUpdateDto.getLastName());
    }

    if (userUpdateDto.getHomeAddress() != null)
    {
      user.setHomeAddress(userUpdateDto.getHomeAddress());
    }

    if (userUpdateDto.getHomeLatitude() != null)
    {
      user.setHomeLatitude(userUpdateDto.getHomeLatitude());
    }

    if (userUpdateDto.getHomeLongitude() != null)
    {
      user.setHomeLongitude(userUpdateDto.getHomeLongitude());
    }

    // Save the updated user
    user = userRepository.save(user);

    return convertToUserProfileDto(user);
  }

  /**
   * Updates a user's preferences.
   *
   * @param email          the email of the user
   * @param preferencesDto the preferences to update
   * @return the updated user profile
   * @throws IllegalStateException if the user is not found
   */
  @Transactional
  public UserProfileDto updateUserPreferences(String email, UserPreferencesDto preferencesDto)
  {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("User not found"));

    // Update user preferences if provided
    if (preferencesDto.getLocationSharingEnabled() != null)
    {
      user.setLocationSharingEnabled(preferencesDto.getLocationSharingEnabled());
    }

    if (preferencesDto.getTwoFactorAuthenticationEnabled() != null)
    {
      if (AdminChecker.isUserAdmin(user))
      {
        user.setIsUsing2FA(true);
      } else
      {
        user.setIsUsing2FA(preferencesDto.getTwoFactorAuthenticationEnabled());

      }
    }


    // Save the updated user
    user = userRepository.save(user);

    return

        convertToUserProfileDto(user);
  }

  /**
   * Get the user's preferences.
   */
  @Transactional(readOnly = true)
  public UserPreferencesDto getUserPreferences(String email)
  {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("User not found"));

    UserPreferencesDto preferencesDto = new UserPreferencesDto();
    preferencesDto.setLocationSharingEnabled(user.getLocationSharingEnabled());
    preferencesDto.setTwoFactorAuthenticationEnabled(user.getIsUsing2FA());

    return preferencesDto;
  }

  /**
   * Gets a user's history (completed activities and reflections).
   *
   * @param email the email of the user
   * @return the user's history
   * @throws IllegalStateException if the user is not found
   */
  public UserHistoryDto getUserHistory(String email)
  {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("User not found"));

    // TODO: Implement retrieval of user's completed activities and reflections
    // For now, return empty lists
    List<GamificationActivityDto> completedActivities = new ArrayList<>();
    List<ReflectionDto> reflections = new ArrayList<>();

    return new UserHistoryDto(completedActivities, reflections);
  }

  /**
   * Gets a user's basic information by their ID.
   *
   * @param id the ID of the user
   * @return the user's basic information
   * @throws IllegalStateException if the user is not found
   */
  public UserBasicInfoDto getUserBasicInfo(Integer id)
  {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new IllegalStateException("User not found"));

    return new UserBasicInfoDto(
        user.getFirstName(),
        user.getLastName(),
        user.getEmail(),
        user.getHousehold() != null ? user.getHousehold().getName() : null,
        user.getEmailVerified()
    );
  }

  /**
   * Converts a User entity to a UserProfileDto.
   *
   * @param user the user entity
   * @return the user profile DTO
   */
  private UserProfileDto convertToUserProfileDto(User user)
  {
    return new UserProfileDto(
        user.getId(),
        user.getEmail(),
        user.getFirstName(),
        user.getLastName(),
        user.getHomeAddress(),
        user.getHomeLatitude(),
        user.getHomeLongitude(),
        user.getLocationSharingEnabled(),
        user.getEmailVerified(),
        user.getHousehold() != null ? user.getHousehold().getId() : null,
        user.getHousehold() != null ? user.getHousehold().getName() : null,
        user.getIsUsing2FA()
    );
  }

  /**
   * Confirms a user's safety using a token received via email.
   *
   * @param token The safety confirmation token
   * @throws IllegalArgumentException if the token is invalid
   * @throws IllegalStateException    if the token has expired
   */
  @Transactional
  public void confirmSafety(String token)
  {
    // Find and validate the token
    EmailToken emailToken = emailTokenRepository.findByToken(token)
        .orElseThrow(() -> new IllegalArgumentException("Ugyldig token. / Invalid token."));

    // Check token type
    if (emailToken.getType() != EmailToken.TokenType.SAFETY_CONFIRMATION)
    {
      throw new IllegalArgumentException("Ugyldig token type. / Invalid token type.");
    }

    // Check if token has expired
    if (emailToken.getExpiresAt().isBefore(LocalDateTime.now()))
    {
      throw new IllegalStateException("Token er utløpt. / Token has expired.");
    }

    User user = emailToken.getUser();
    LocalDateTime now = LocalDateTime.now();

    // Delete all existing safety confirmations for this user
    safetyConfirmationRepository.deleteByUser(user);

    // Create new confirmation
    SafetyConfirmation confirmation = new SafetyConfirmation(user, true, now);
    safetyConfirmationRepository.save(confirmation);
  }

  /**
   * Requests safety confirmation from all other members of the user's household. Each member will
   * receive an email with a unique token to confirm their safety.
   *
   * @param email The email of the user requesting safety confirmation
   * @throws IllegalStateException if the user is not found or does not belong to a household
   */
  @Transactional
  public void requestSafetyConfirmation(String email)
  {
    User requestingUser = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("Bruker ikke funnet. / User not found."));

    if (requestingUser.getHousehold() == null)
    {
      throw new IllegalStateException(
          "Du må være medlem av en husstand for å be om sikkerhetsbekreftelser. / You must be a member of a household to request safety confirmations.");
    }

    // Automatically mark the requesting user as safe
    LocalDateTime now = LocalDateTime.now();
    // Delete any existing safety confirmations for the requesting user
    safetyConfirmationRepository.deleteByUser(requestingUser);
    // Create new safety confirmation for the requesting user
    SafetyConfirmation requestingUserConfirmation = new SafetyConfirmation(requestingUser, true,
        now);
    safetyConfirmationRepository.save(requestingUserConfirmation);

    List<User> householdMembers = userRepository.findByHousehold(requestingUser.getHousehold());
    if (householdMembers.isEmpty() || householdMembers.size() == 1)
    {
      throw new IllegalStateException(
          "Ingen andre medlemmer i husstanden. / No other members in the household.");
    }

    try
    {
      for (User member : householdMembers)
      {
        // Skip sending to the requesting user
        if (member.getEmail().equals(email))
        {
          continue;
        }

        // Generate unique token for this member
        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(168); // 1 week

        // Create and save token
        EmailToken safetyToken = new EmailToken(
            member,
            token,
            EmailToken.TokenType.SAFETY_CONFIRMATION,
            expiresAt
        );
        emailTokenRepository.save(safetyToken);

        // Send email with the unique token
        emailService.sendSafetyConfirmationEmail(requestingUser, member, token);

        // Create notification for the safety request
        String requestingUserName =
            requestingUser.getName() != null ? requestingUser.getName() : "et husstandsmedlem";
        notificationService.createSafetyRequestNotification(member, requestingUserName);
      }
    } catch (Exception e)
    {
      throw e;
    }
  }

  /**
   * Checks if a user has confirmed their safety within the last 24 hours.
   *
   * @param userId The ID of the user to check
   * @return true if the user has confirmed their safety within the last 24 hours, false otherwise
   * @throws IllegalStateException if the user is not found
   */
  public boolean isSafe(Integer userId)
  {
    // Verify user exists
    userRepository.findById(userId)
        .orElseThrow(() -> new IllegalStateException("User not found"));

    // Check if user has a safety confirmation where is_safe is true
    Optional<SafetyConfirmation> safetyConfirmation = safetyConfirmationRepository.findByUser(
        userRepository.getReferenceById(userId));

    if (!safetyConfirmation.isPresent() || !safetyConfirmation.get().getIsSafe())
    {
      return false;
    }

    // Check if the confirmation is less than 24 hours old
    LocalDateTime confirmationTime = safetyConfirmation.get().getSafeAt();
    LocalDateTime oneDayAgo = LocalDateTime.now().minusHours(24);

    return confirmationTime.isAfter(oneDayAgo);
  }
}
