package stud.ntnu.backend.service.user;

import org.springframework.stereotype.Service;
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

/**
 * Service for managing users. Handles retrieval, updating, and deletion of users. Note: User
 * creation is handled by AuthService.
 */
@Service
public class UserService {

  private final UserRepository userRepository;
  private final EmailTokenRepository emailTokenRepository;
  private final SafetyConfirmationRepository safetyConfirmationRepository;

  /**
   * Constructor for dependency injection.
   *
   * @param userRepository repository for user operations
   * @param emailTokenRepository repository for email tokens
   * @param safetyConfirmationRepository repository for safety confirmations
   */
  public UserService(UserRepository userRepository,
                    EmailTokenRepository emailTokenRepository,
                    SafetyConfirmationRepository safetyConfirmationRepository) {
    this.userRepository = userRepository;
    this.emailTokenRepository = emailTokenRepository;
    this.safetyConfirmationRepository = safetyConfirmationRepository;
  }

  /**
   * Retrieves all users.
   *
   * @return list of all users
   */
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  /**
   * Retrieves a user by their ID.
   *
   * @param id the ID of the user
   * @return an Optional containing the user if found
   */
  public Optional<User> getUserById(Integer id) {
    return userRepository.findById(id);
  }

  /**
   * Retrieves a user by their email.
   *
   * @param email the email of the user
   * @return an Optional containing the user if found
   */
  public Optional<User> getUserByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  public Integer getUserIdByEmail(String email) {
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
  public User saveUser(User user) {
    return userRepository.save(user);
  }

  /**
   * Deletes a user by their ID.
   *
   * @param id the ID of the user to delete
   */
  public void deleteUser(Integer id) {
    userRepository.deleteById(id);
  }

  /**
   * Gets the profile of a user by their email.
   *
   * @param email the email of the user
   * @return the user's profile
   * @throws IllegalStateException if the user is not found
   */
  public UserProfileDto getUserProfile(String email) {
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
  public UserProfileDto updateUserProfile(String email, UserUpdateDto userUpdateDto) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("User not found"));

    // Update user fields if provided
    if (userUpdateDto.getFirstName() != null) {
      user.setFirstName(userUpdateDto.getFirstName());
    }

    if (userUpdateDto.getLastName() != null) {
      user.setLastName(userUpdateDto.getLastName());
    }

    if (userUpdateDto.getHomeAddress() != null) {
      user.setHomeAddress(userUpdateDto.getHomeAddress());
    }

    if (userUpdateDto.getHomeLatitude() != null) {
      user.setHomeLatitude(userUpdateDto.getHomeLatitude());
    }

    if (userUpdateDto.getHomeLongitude() != null) {
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
  public UserProfileDto updateUserPreferences(String email, UserPreferencesDto preferencesDto) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("User not found"));

    // Update user preferences if provided
    if (preferencesDto.getLocationSharingEnabled() != null) {
      user.setLocationSharingEnabled(preferencesDto.getLocationSharingEnabled());
    }

    // Save the updated user
    user = userRepository.save(user);

    return convertToUserProfileDto(user);
  }

  /**
   * Gets a user's history (completed activities and reflections).
   *
   * @param email the email of the user
   * @return the user's history
   * @throws IllegalStateException if the user is not found
   */
  public UserHistoryDto getUserHistory(String email) {
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
  public UserBasicInfoDto getUserBasicInfo(Integer id) {
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
  private UserProfileDto convertToUserProfileDto(User user) {
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
        user.getHousehold() != null ? user.getHousehold().getName() : null
    );
  }

  /**
   * Confirms a user's safety using a token received via email.
   *
   * @param token The safety confirmation token
   * @throws IllegalArgumentException if the token is invalid
   * @throws IllegalStateException if the token has expired
   */
  @Transactional
  public void confirmSafety(String token) {
    // Find and validate the token
    EmailToken emailToken = emailTokenRepository.findByToken(token)
        .orElseThrow(() -> new IllegalArgumentException("Ugyldig token. / Invalid token."));

    // Check token type
    if (emailToken.getType() != EmailToken.TokenType.SAFETY_CONFIRMATION) {
      throw new IllegalArgumentException("Ugyldig token type. / Invalid token type.");
    }

    // Check if token has expired
    if (emailToken.getExpiresAt().isBefore(LocalDateTime.now())) {
      throw new IllegalStateException("Token er utløpt. / Token has expired.");
    }

    User user = emailToken.getUser();
    LocalDateTime now = LocalDateTime.now();

    // Find existing safety confirmation or create new one
    Optional<SafetyConfirmation> existingConfirmation = safetyConfirmationRepository.findByUser(user);
    
    if (existingConfirmation.isPresent()) {
      // Update existing confirmation
      SafetyConfirmation confirmation = existingConfirmation.get();
      confirmation.setIsSafe(true);
      confirmation.setSafeAt(now);
      safetyConfirmationRepository.save(confirmation);
    } else {
      // Create new confirmation
      SafetyConfirmation confirmation = new SafetyConfirmation(user, true, now);
      safetyConfirmationRepository.save(confirmation);
    }
  }
}
