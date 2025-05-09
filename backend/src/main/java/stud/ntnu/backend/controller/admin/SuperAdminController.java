package stud.ntnu.backend.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.backend.dto.user.UserInfoDto;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.security.AdminChecker;
import stud.ntnu.backend.service.admin.SuperAdminService;
import stud.ntnu.backend.service.user.UserService;

import java.security.Principal;
import java.util.List;

/**
 * REST controller for super administrator operations. Provides endpoints for managing admin users,
 * including retrieving admin information, adding and revoking admin privileges.
 */
@RestController
@RequestMapping("/api/super-admin")
@Tag(name = "Super Administrator", description = "Operations related to super administrator management")
public class SuperAdminController {

  private final SuperAdminService superAdminService;
  private final UserService userService;

  /**
   * Constructs a new SuperAdminController with the required services.
   *
   * @param superAdminService service for super admin operations
   * @param userService       service for user operations
   */
  public SuperAdminController(SuperAdminService superAdminService, UserService userService) {
    this.superAdminService = superAdminService;
    this.userService = userService;
  }

  /**
   * Error message for unauthorized access attempts
   */
  private static final String ONLY_SUPER_ADMIN = "Only super-administrators can access this resource";

  /**
   * Retrieves all administrators in the system. Only accessible by super administrators.
   *
   * @param principal the authenticated user making the request
   * @return ResponseEntity containing either: - List of UserInfoDto objects representing all admins
   * - 403 Forbidden response if unauthorized - 400 Bad Request with error message if operation
   * fails
   */
  @Operation(summary = "Get all administrators", description = "Retrieves a list of all administrators in the system. Only accessible by super administrators.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved all administrators", 
          content = @Content(schema = @Schema(implementation = UserInfoDto.class))),
      @ApiResponse(responseCode = "403", description = "Access forbidden - only super administrators can access this resource", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "400", description = "Bad request - operation failed", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @GetMapping("/all")
  public ResponseEntity<?> getAdmins(Principal principal) {
    try {
      if (!AdminChecker.isCurrentUserSuperAdmin(principal, userService)) {
        return ResponseEntity.status(403).body(ONLY_SUPER_ADMIN);
      }

      List<UserInfoDto> admins = superAdminService.getAllAdmins();
      return ResponseEntity.ok(admins);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Retrieves user information by email address. Only accessible by super administrators.
   *
   * @param principal the authenticated user making the request
   * @param email     the email address of the user to look up
   * @return ResponseEntity containing either: - UserInfoDto with user's email and ID - 403
   * Forbidden response if unauthorized - 400 Bad Request with error message if user not found
   */
  @Operation(summary = "Get user information by email", description = "Retrieves user information by email address. Only accessible by super administrators.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved user information", 
          content = @Content(schema = @Schema(implementation = UserInfoDto.class))),
      @ApiResponse(responseCode = "403", description = "Access forbidden - only super administrators can access this resource", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "400", description = "Bad request - user not found", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @GetMapping("/user-info/{email}")
  public ResponseEntity<?> getIdByEmail(Principal principal, @PathVariable String email) {
    try {
      if (!AdminChecker.isCurrentUserSuperAdmin(principal, userService)) {
        return ResponseEntity.status(403).body(ONLY_SUPER_ADMIN);
      }
      User user = userService.getUserByEmail(email)
          .orElseThrow(() -> new RuntimeException("User not found"));

      UserInfoDto userInfoDto = new UserInfoDto(user.getEmail(), user.getId());
      return ResponseEntity.ok(userInfoDto);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Revokes administrator privileges from a user. Only accessible by super administrators.
   *
   * @param principal the authenticated user making the request
   * @param id        the ID of the user to revoke admin privileges from
   * @return ResponseEntity containing either: - Success message if admin access is revoked - 403
   * Forbidden response if unauthorized - 400 Bad Request with error message if user not found or
   * not an admin
   */
  @Operation(summary = "Revoke admin privileges", description = "Revokes administrator privileges from a user. Only accessible by super administrators.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully revoked admin privileges", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "403", description = "Access forbidden - only super administrators can access this resource", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "400", description = "Bad request - user not found or not an admin", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @PutMapping("/revoke/{id}")
  public ResponseEntity<?> revokeAdminAccess(Principal principal, @PathVariable Integer id) {
    try {
      if (!AdminChecker.isCurrentUserSuperAdmin(principal, userService)) {
        return ResponseEntity.status(403).body(ONLY_SUPER_ADMIN);
      }

      User user = userService.getUserById(id)
          .orElseThrow(() -> new RuntimeException("User not found"));
      if (!"ADMIN".equals(user.getRole().getName())) {
        return ResponseEntity.status(400).body("The user does not have an admin role to revoke");
      }

      superAdminService.revokeAdminAccess(id);
      return ResponseEntity.ok("Admin access revoked successfully, user now has user role");
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Grants administrator privileges to a user. Only accessible by super administrators.
   *
   * @param principal the authenticated user making the request
   * @param id        the ID of the user to grant admin privileges to
   * @return ResponseEntity containing either: - Success message if admin access is granted - 403
   * Forbidden response if unauthorized - 400 Bad Request with error message if user not found or
   * already an admin
   */
  @Operation(summary = "Grant admin privileges", description = "Grants administrator privileges to a user. Only accessible by super administrators.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully granted admin privileges", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "403", description = "Access forbidden - only super administrators can access this resource", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "400", description = "Bad request - user not found or already an admin", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @PutMapping("/add/{id}")
  public ResponseEntity<?> addAdminAccess(Principal principal, @PathVariable Integer id) {
    try {
      if (!AdminChecker.isCurrentUserSuperAdmin(principal, userService)) {
        return ResponseEntity.status(403).body(ONLY_SUPER_ADMIN);
      }

      User user = userService.getUserById(id)
          .orElseThrow(() -> new RuntimeException("User not found"));
      if (!"USER".equals(user.getRole().getName())) {
        return ResponseEntity.status(400)
            .body("The user does not have a user role to promote to admin");
      }
      if ("ADMIN".equals(user.getRole().getName())) {
        return ResponseEntity.status(400).body("The user already has an admin role");
      }

      superAdminService.addAdminAccess(id);
      return ResponseEntity.ok("Admin access granted successfully, user now has admin role");
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}
