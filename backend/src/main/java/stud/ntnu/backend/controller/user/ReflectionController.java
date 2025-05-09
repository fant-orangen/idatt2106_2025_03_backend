package stud.ntnu.backend.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import stud.ntnu.backend.dto.user.CreateReflectionDto;
import stud.ntnu.backend.dto.user.ReflectionResponseDto;
import stud.ntnu.backend.dto.user.UpdateReflectionDto;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.service.group.GroupService;
import stud.ntnu.backend.service.user.ReflectionService;
import stud.ntnu.backend.service.user.UserService;

/**
 * Controller for managing user-generated crisis reflections.
 * <p>
 * This controller provides endpoints for users to create, read, update, and delete their crisis
 * reflections. Reflections can be personal or shared with household members and groups. The
 * controller handles authentication and authorization to ensure users can only access and modify
 * their own reflections or those shared with them.
 * <p>
 */
@RestController
@RequestMapping("/api/user/reflections")
@Tag(name = "Reflections", description = "Operations for managing user-generated crisis reflections")
public class ReflectionController {

  private final ReflectionService reflectionService;
  private final UserService userService;
  private final GroupService groupService;

  /**
   * Constructs a new ReflectionController with required services.
   *
   * @param reflectionService service for managing reflections
   * @param userService       service for user operations
   * @param groupService      service for group operations
   */
  public ReflectionController(ReflectionService reflectionService, UserService userService,
      GroupService groupService) {
    this.reflectionService = reflectionService;
    this.userService = userService;
    this.groupService = groupService;
  }

  /**
   * Retrieves all personal reflections for the authenticated user.
   *
   * @param principal the authenticated user's principal
   * @param pageable  pagination parameters
   * @return ResponseEntity containing a page of the user's reflections
   */
  @Operation(summary = "Get personal reflections", description = "Retrieves all personal reflections for the authenticated user.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved personal reflections", 
          content = @Content(schema = @Schema(implementation = ReflectionResponseDto.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - user not found", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @GetMapping("/my")
  public ResponseEntity<Page<ReflectionResponseDto>> getMyReflections(
      Principal principal,
      Pageable pageable) {
    try {
      User user = userService.getUserByEmail(principal.getName())
          .orElseThrow(() -> new IllegalStateException("User not found"));
      Page<ReflectionResponseDto> reflections = reflectionService.getReflectionsByUserId(
          user.getId(), pageable);
      return ResponseEntity.ok(reflections);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Retrieves all shared reflections visible to the authenticated user. This includes reflections
   * shared with the user's household and groups.
   *
   * @param principal the authenticated user's principal
   * @param pageable  pagination parameters
   * @return ResponseEntity containing a page of shared reflections
   */
  @Operation(summary = "Get shared reflections", description = "Retrieves all shared reflections visible to the authenticated user, including those shared with the user's household and groups.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved shared reflections", 
          content = @Content(schema = @Schema(implementation = ReflectionResponseDto.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - user not found", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @GetMapping("/shared")
  public ResponseEntity<Page<ReflectionResponseDto>> getSharedReflections(
      Principal principal,
      Pageable pageable) {
    try {
      User user = userService.getUserByEmail(principal.getName())
          .orElseThrow(() -> new IllegalStateException("User not found"));
      Page<ReflectionResponseDto> reflections = reflectionService.getSharedReflectionsVisibleToUser(
          user.getId(), pageable);
      return ResponseEntity.ok(reflections);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Retrieves all shared reflections from the user's household. Requires the user to be a member of
   * a household.
   *
   * @param principal the authenticated user's principal
   * @param pageable  pagination parameters
   * @return ResponseEntity containing a page of household reflections or 400 if user has no
   * household
   */
  @Operation(summary = "Get household reflections", description = "Retrieves all shared reflections from the user's household. Requires the user to be a member of a household.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved household reflections", 
          content = @Content(schema = @Schema(implementation = ReflectionResponseDto.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - user not found or not in a household", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @GetMapping("/household")
  public ResponseEntity<Page<ReflectionResponseDto>> getHouseholdReflections(
      Principal principal,
      Pageable pageable) {
    try {
      User user = userService.getUserByEmail(principal.getName())
          .orElseThrow(() -> new IllegalStateException("User not found"));

      if (user.getHousehold() == null) {
        return ResponseEntity.status(400).body(null);
      }

      Integer householdId = user.getHousehold().getId();
      Page<ReflectionResponseDto> reflections = reflectionService.getSharedReflectionsByHouseholdId(
          householdId, pageable);
      return ResponseEntity.ok(reflections);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Retrieves all shared reflections from all groups the user's household is a member of.
   *
   * @param principal the authenticated user's principal
   * @param pageable  pagination parameters
   * @return ResponseEntity containing a page of group reflections
   */
  @Operation(summary = "Get group reflections", description = "Retrieves all shared reflections from all groups the user's household is a member of.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved group reflections", 
          content = @Content(schema = @Schema(implementation = ReflectionResponseDto.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - user not found", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @GetMapping("/groups")
  public ResponseEntity<Page<ReflectionResponseDto>> getGroupReflections(
      Principal principal,
      Pageable pageable) {
    try {
      String email = principal.getName();
      userService.getUserByEmail(email)
          .orElseThrow(() -> new IllegalStateException("User not found"));

      Page<ReflectionResponseDto> reflections = reflectionService.getSharedReflectionsFromAllUserGroups(
          email, pageable);
      return ResponseEntity.ok(reflections);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Creates a new reflection for the authenticated user.
   *
   * @param createReflectionDto the reflection data to create
   * @param principal           the authenticated user's principal
   * @return ResponseEntity containing the created reflection
   */
  @Operation(summary = "Create reflection", description = "Creates a new reflection for the authenticated user.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully created reflection", 
          content = @Content(schema = @Schema(implementation = ReflectionResponseDto.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid reflection data or user not found", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @PostMapping
  public ResponseEntity<ReflectionResponseDto> createReflection(
      @Valid @RequestBody CreateReflectionDto createReflectionDto,
      Principal principal) {
    try {
      User user = userService.getUserByEmail(principal.getName())
          .orElseThrow(() -> new IllegalStateException("User not found"));
      ReflectionResponseDto reflection = reflectionService.createReflection(user.getId(),
          createReflectionDto);
      return ResponseEntity.ok(reflection);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Updates an existing reflection. Only the owner of the reflection can update it.
   *
   * @param id                  the ID of the reflection to update
   * @param updateReflectionDto the updated reflection data
   * @param principal           the authenticated user's principal
   * @return ResponseEntity containing the updated reflection, or 403 if not authorized
   */
  @Operation(summary = "Update reflection", description = "Updates an existing reflection. Only the owner of the reflection can update it.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully updated reflection", 
          content = @Content(schema = @Schema(implementation = ReflectionResponseDto.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid reflection data or user not found", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "403", description = "Forbidden - user not authorized to update this reflection", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @PutMapping("/{id}")
  public ResponseEntity<ReflectionResponseDto> updateReflection(
      @PathVariable Integer id,
      @Valid @RequestBody UpdateReflectionDto updateReflectionDto,
      Principal principal) {
    try {
      User user = userService.getUserByEmail(principal.getName())
          .orElseThrow(() -> new IllegalStateException("User not found"));
      ReflectionResponseDto reflection = reflectionService.updateReflection(id, user.getId(),
          updateReflectionDto);
      return ResponseEntity.ok(reflection);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(403).build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Soft deletes a reflection by marking it as deleted. Only the owner of the reflection can delete
   * it.
   *
   * @param id        the ID of the reflection to delete
   * @param principal the authenticated user's principal
   * @return ResponseEntity with status 200 if successful, 403 if not authorized, 400 if error
   */
  @Operation(summary = "Delete reflection", description = "Soft deletes a reflection by marking it as deleted. Only the owner of the reflection can delete it.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully deleted reflection"),
      @ApiResponse(responseCode = "400", description = "Bad request - reflection not found or user not found", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "403", description = "Forbidden - user not authorized to delete this reflection", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteReflection(
      @PathVariable Integer id,
      Principal principal) {
    try {
      User user = userService.getUserByEmail(principal.getName())
          .orElseThrow(() -> new IllegalStateException("User not found"));
      reflectionService.deleteReflection(id, user.getId());
      return ResponseEntity.ok().build();
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(403).build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }
}
