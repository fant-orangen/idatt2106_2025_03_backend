package stud.ntnu.backend.controller.user;

import java.security.Principal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.backend.dto.user.UserProfileDto;
import stud.ntnu.backend.dto.user.UserPreferencesDto;
import stud.ntnu.backend.dto.user.UserHistoryDto;
import stud.ntnu.backend.dto.user.UserUpdateDto;
import stud.ntnu.backend.dto.user.UserBasicInfoDto;
import stud.ntnu.backend.service.user.UserService;

/**
 * Manages user profile operations. Includes viewing and updating personal details (name, address,
 * location sharing), managing email verification status, and retrieving gamification and reflection
 * history.
 * <p>
 * Based on Visjonsdokument 2025 for Krisefikser.no.
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
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

  /**
   * Returns a user's basic information by their ID.
   *
   * @param id the ID of the user to retrieve
   * @return ResponseEntity containing the user's basic information or an error message
   */
  @GetMapping("/{id}/basic-info")
  public ResponseEntity<?> getUserBasicInfo(@PathVariable Integer id) {
    try {
      UserBasicInfoDto userBasicInfo = userService.getUserBasicInfo(id);
      return ResponseEntity.ok(userBasicInfo);
    } catch (IllegalStateException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Confirms a user's safety using a token received via email.
   *
   * @param token The safety confirmation token
   * @return ResponseEntity with status 200 OK if successful, or an error message
   */
  @PostMapping("/confirm-safety")
  public ResponseEntity<?> confirmSafety(@RequestParam String token) {
    try {
      userService.confirmSafety(token);
      return ResponseEntity.ok("Din sikkerhet er bekreftet. / Your safety has been confirmed.");
    } catch (IllegalArgumentException | IllegalStateException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body("En feil oppstod. Vennligst prøv igjen senere. / An error occurred. Please try again later.");
    }
  }
}
