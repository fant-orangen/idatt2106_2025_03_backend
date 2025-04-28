package stud.ntnu.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.backend.dto.user.UserProfileDto;
import stud.ntnu.backend.dto.user.UserPreferencesDto;
import stud.ntnu.backend.dto.user.UserHistoryDto;
import stud.ntnu.backend.dto.user.UserUpdateDto;
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

  public UserController(UserService userService) {
    this.userService = userService;
  }

  /**
   * Returns the logged-in user's full profile.
   *
   * @return ResponseEntity containing the user's profile information
   */
  @GetMapping("/me")
  public ResponseEntity<UserProfileDto> getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

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
  @PatchMapping("/me/preferences")
  public ResponseEntity<UserProfileDto> updateUserPreferences(
      @RequestBody UserPreferencesDto preferencesDto) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    UserProfileDto updatedProfile = userService.updateUserPreferences(email, preferencesDto);
    return ResponseEntity.ok(updatedProfile);
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
