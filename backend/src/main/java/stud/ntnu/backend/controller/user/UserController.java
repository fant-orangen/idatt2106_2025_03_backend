package stud.ntnu.backend.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.backend.dto.user.UserBasicInfoDto;
import stud.ntnu.backend.dto.user.UserHistoryDto;
import stud.ntnu.backend.dto.user.UserPreferencesDto;
import stud.ntnu.backend.dto.user.UserProfileDto;
import stud.ntnu.backend.dto.user.UserUpdateDto;
import stud.ntnu.backend.service.user.UserService;

/**
 * Controller for managing user profile operations in the Krisefikser.no application.
 * <p>
 * This controller handles:
 * <ul>
 *   <li>User profile management (viewing and updating personal details)</li>
 *   <li>User preferences management</li>
 *   <li>User history tracking (activities and reflections)</li>
 *   <li>Safety confirmation system for household members</li>
 * </ul>
 * <p>
 */
@RestController
@RequestMapping("/api/user")
@Tag(name = "User Management", description = "Operations for managing user profiles, preferences, history, and safety confirmations")
public class UserController {

  private final UserService userService;

  /**
   * Constructs a new UserController with the specified UserService.
   *
   * @param userService the service for handling user-related operations
   */
  public UserController(UserService userService) {
    this.userService = userService;
  }

  /**
   * Retrieves the complete profile of the currently authenticated user.
   *
   * @param principal the Principal object representing the current authenticated user
   * @return ResponseEntity containing the user's complete profile information
   */
  @Operation(summary = "Get current user profile", description = "Retrieves the complete profile of the currently authenticated user.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved user profile", 
          content = @Content(schema = @Schema(implementation = UserProfileDto.class)))
  })
  @GetMapping("/me")
  public ResponseEntity<UserProfileDto> getCurrentUser(Principal principal) {
    String email = principal.getName();
    UserProfileDto userProfile = userService.getUserProfile(email);
    return ResponseEntity.ok(userProfile);
  }

  /**
   * Updates the profile information of the currently authenticated user.
   *
   * @param userUpdateDto the DTO containing the updated user information
   * @return ResponseEntity containing the updated user profile
   */
  @Operation(summary = "Update user profile", description = "Updates the profile information of the currently authenticated user.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully updated user profile", 
          content = @Content(schema = @Schema(implementation = UserProfileDto.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid profile data", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @PutMapping("/me")
  public ResponseEntity<UserProfileDto> updateCurrentUser(
      @RequestBody UserUpdateDto userUpdateDto) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    UserProfileDto updatedProfile = userService.updateUserProfile(email, userUpdateDto);
    return ResponseEntity.ok(updatedProfile);
  }

  /**
   * Updates the preferences of the currently authenticated user.
   *
   * @param preferencesDto the DTO containing the updated user preferences
   * @return ResponseEntity containing the updated user profile
   */
  @Operation(summary = "Update user preferences", description = "Updates the preferences of the currently authenticated user.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully updated user preferences", 
          content = @Content(schema = @Schema(implementation = UserProfileDto.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid preferences data", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @PatchMapping("/me/preferences")
  public ResponseEntity<UserProfileDto> updateUserPreferences(
      @RequestBody UserPreferencesDto preferencesDto) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    UserProfileDto updatedProfile = userService.updateUserPreferences(email, preferencesDto);
    return ResponseEntity.ok(updatedProfile);
  }

    /**
     * Retrieves the preferences of the currently authenticated user.
     *
     * @return ResponseEntity containing the user's preferences
     */
    @GetMapping("/me/preferences")
    public ResponseEntity<UserPreferencesDto> getUserPreferences() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserPreferencesDto userPreferences = userService.getUserPreferences(email);
        return ResponseEntity.ok(userPreferences);
    }

  /**
   * Retrieves the complete history of the currently authenticated user, including completed
   * activities and reflections.
   *
   * @return ResponseEntity containing the user's complete history
   */
  @Operation(summary = "Get user history", description = "Retrieves the complete history of the currently authenticated user, including completed activities and reflections.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved user history", 
          content = @Content(schema = @Schema(implementation = UserHistoryDto.class)))
  })
  @GetMapping("/me/history")
  public ResponseEntity<UserHistoryDto> getUserHistory() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    UserHistoryDto userHistory = userService.getUserHistory(email);
    return ResponseEntity.ok(userHistory);
  }

  /**
   * Retrieves basic information about a specific user by their ID.
   *
   * @param id the unique identifier of the user
   * @return ResponseEntity containing the user's basic information or an error message
   * @throws IllegalStateException if the user cannot be found or accessed
   */
  @Operation(summary = "Get user basic info", description = "Retrieves basic information about a specific user by their ID.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved user basic info", 
          content = @Content(schema = @Schema(implementation = UserBasicInfoDto.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - user not found or not accessible", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
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
   * Confirms a user's safety status using a verification token received via email.
   *
   * @param token the unique safety confirmation token
   * @return ResponseEntity with success message if confirmed, or error message if failed
   * @throws IllegalArgumentException if the token is invalid
   * @throws IllegalStateException    if the confirmation process fails
   */
  @Operation(summary = "Confirm safety", description = "Confirms a user's safety status using a verification token received via email.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully confirmed safety", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid token", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "500", description = "Internal server error", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @GetMapping("/confirm-safety")
  public ResponseEntity<?> confirmSafety(@RequestParam String token) {
    try {
      userService.confirmSafety(token);
      return ResponseEntity.ok("Din sikkerhet er bekreftet. / Your safety has been confirmed.");
    } catch (IllegalArgumentException | IllegalStateException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.internalServerError()
          .body(
              "En feil oppstod. Vennligst prøv igjen senere. / An error occurred. Please try again later.");
    }
  }

  /**
   * Initiates a safety confirmation request for all members of the user's household. Each member
   * will receive a unique confirmation token via email.
   *
   * @param principal the Principal object representing the current authenticated user
   * @return ResponseEntity with success message if requests sent, or error message if failed
   * @throws IllegalArgumentException if the request is invalid
   * @throws IllegalStateException    if the request process fails
   */
  @Operation(summary = "Request safety confirmation", description = "Initiates a safety confirmation request for all members of the user's household.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully sent safety confirmation requests", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid request", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "500", description = "Internal server error", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @PostMapping("/confirm-safety/requests")
  public ResponseEntity<?> requestSafetyConfirmation(Principal principal) {
    try {
      userService.requestSafetyConfirmation(principal.getName());
      return ResponseEntity.ok(
          "Sikkerhetsbekreftelser er sendt til alle husstandsmedlemmer. / Safety confirmations have been sent to all household members.");
    } catch (IllegalArgumentException | IllegalStateException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.internalServerError()
          .body(
              "En feil oppstod. Vennligst prøv igjen senere. / An error occurred. Please try again later.");
    }
  }

  /**
   * Checks the safety status of a specific user.
   *
   * @param userId the unique identifier of the user to check
   * @return ResponseEntity with boolean indicating safety status, or error message if check fails
   * @throws IllegalArgumentException if the user ID is invalid
   * @throws IllegalStateException    if the safety check process fails
   */
  @Operation(summary = "Check user safety status", description = "Checks the safety status of a specific user.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully checked safety status", 
          content = @Content(schema = @Schema(type = "boolean"))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid user ID", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @GetMapping("/confirm-safety/is-safe")
  public ResponseEntity<?> isSafe(@RequestParam Integer userId) {
    try {
      boolean isSafe = userService.isSafe(userId);
      return ResponseEntity.ok(isSafe);
    } catch (IllegalArgumentException | IllegalStateException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}
