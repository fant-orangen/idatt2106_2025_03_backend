package stud.ntnu.backend.service;

import org.springframework.stereotype.Service;
import stud.ntnu.backend.repository.UserRepository;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.dto.UserProfileDto;
import stud.ntnu.backend.dto.UserUpdateDto;
import stud.ntnu.backend.dto.UserPreferencesDto;
import stud.ntnu.backend.dto.UserHistoryDto;
import stud.ntnu.backend.dto.UserHistoryDto.GamificationActivityDto;
import stud.ntnu.backend.dto.UserHistoryDto.ReflectionDto;

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

  /**
   * Constructor for dependency injection.
   *
   * @param userRepository repository for user operations
   */
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
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
   * @param email the email of the user
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
   * @param email the email of the user
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
}
