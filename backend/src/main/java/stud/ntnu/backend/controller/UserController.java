package stud.ntnu.backend.controller;

import java.security.Principal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.backend.dto.user.UserProfileDto;
import stud.ntnu.backend.dto.user.UserPreferencesDto;
import stud.ntnu.backend.dto.user.UserHistoryDto;
import stud.ntnu.backend.dto.user.UserUpdateDto;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.repository.user.UserRepository;
import stud.ntnu.backend.service.UserService;

/**
 * Manages user profile operations. Includes viewing and updating personal details (name, address,
 * location sharing), managing email verification status, and retrieving gamification and reflection
 * history.
 * <p>
 * Based on Visjonsdokument 2025 for Krisefikser.no.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;
  private final UserRepository userRepository;

  public UserController(UserService userService, UserRepository userRepository) {
    this.userService = userService;
    this.userRepository = userRepository;
  }

  /**
   * Returns the logged-in user's full profile.
   *
   * @param principal the Principal representing the current user
   * @return ResponseEntity containing the user's profile information
   */
  @GetMapping("/me")
  public ResponseEntity<UserProfileDto> getCurrentUser(Principal principal) {
    String email = principal.getName();
    UserProfileDto userProfile = userService.getUserProfile(email);
    return ResponseEntity.ok(userProfile);
  }

  /**
   * Updates the logged-in user's profile information.
   *
   * @param userUpdateDto the user information to update
   * @return ResponseEntity containing the updated user profile
   */
  @PutMapping("/me")
  public ResponseEntity<UserProfileDto> updateCurrentUser(
      @RequestBody UserUpdateDto userUpdateDto) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    UserProfileDto updatedProfile = userService.updateUserProfile(email, userUpdateDto);
    return ResponseEntity.ok(updatedProfile);
  }

  /**
   * Updates the logged-in user's preferences.
   *
   * @param preferencesDto the preferences to update
   * @return ResponseEntity containing the updated user profile
   */
  @PatchMapping("/me/preferences/update")
  public ResponseEntity<UserProfileDto> updateUserPreferences(
      @RequestBody UserPreferencesDto preferencesDto) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    UserProfileDto updatedProfile = userService.updateUserPreferences(email, preferencesDto);
    return ResponseEntity.ok(updatedProfile);
  }

  @GetMapping("/me/preferences/get")
  public ResponseEntity<UserPreferencesDto> getUserPreferences() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    User currentUser = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));
    UserPreferencesDto preferencesDto = new UserPreferencesDto(
        currentUser.getLocationSharingEnabled(),
        false, //TODO: fix notificationsEnabled
        currentUser.getIsUsing2FA()
    );
    return ResponseEntity.ok(preferencesDto);
  }

  /**
   * Returns the logged-in user's history (completed activities and reflections).
   *
   * @return ResponseEntity containing the user's history
   */
  @GetMapping("/me/history")
  public ResponseEntity<UserHistoryDto> getUserHistory() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    UserHistoryDto userHistory = userService.getUserHistory(email);
    return ResponseEntity.ok(userHistory);
  }
}
