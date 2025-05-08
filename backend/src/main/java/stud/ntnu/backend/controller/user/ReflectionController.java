package stud.ntnu.backend.controller.user;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.backend.dto.user.CreateReflectionDto;
import stud.ntnu.backend.dto.user.ReflectionResponseDto;
import stud.ntnu.backend.dto.user.UpdateReflectionDto;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.service.group.GroupService;
import stud.ntnu.backend.service.user.ReflectionService;
import stud.ntnu.backend.service.user.UserService;

import java.security.Principal;

/**
 * Handles user-generated crisis reflections. Users can write, store, view, and optionally share
 * their crisis experiences post-event as part of learning and adaptation.
 * <p>
 * Based on Visjonsdokument 2025 for Krisefikser.no.
 */
@RestController
@RequestMapping("/api/user/reflections")
public class ReflectionController {

  private final ReflectionService reflectionService;
  private final UserService userService;
  private final GroupService groupService;
  private final Logger log = LoggerFactory.getLogger(ReflectionController.class);

  public ReflectionController(ReflectionService reflectionService, UserService userService, GroupService groupService) {
    this.reflectionService = reflectionService;
    this.userService = userService;
    this.groupService = groupService;
  }

  /**
   * Get all reflections for the current user.
   *
   * @param principal the authenticated user
   * @param pageable pagination information
   * @return a page of reflections
   */
  @GetMapping("/my")
  public ResponseEntity<Page<ReflectionResponseDto>> getMyReflections(
      Principal principal,
      Pageable pageable) {
    try {
      User user = userService.getUserByEmail(principal.getName())
          .orElseThrow(() -> new IllegalStateException("User not found"));
      Page<ReflectionResponseDto> reflections = reflectionService.getReflectionsByUserId(user.getId(), pageable);
      return ResponseEntity.ok(reflections);
    } catch (Exception e) {
      log.error("Error getting user reflections", e);
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Get all shared reflections visible to the current user (from their household and groups).
   *
   * @param principal the authenticated user
   * @param pageable pagination information
   * @return a page of shared reflections
   */
  @GetMapping("/shared")
  public ResponseEntity<Page<ReflectionResponseDto>> getSharedReflections(
      Principal principal,
      Pageable pageable) {
    try {
      User user = userService.getUserByEmail(principal.getName())
          .orElseThrow(() -> new IllegalStateException("User not found"));
      Page<ReflectionResponseDto> reflections = reflectionService.getSharedReflectionsVisibleToUser(user.getId(), pageable);
      return ResponseEntity.ok(reflections);
    } catch (Exception e) {
      log.error("Error getting shared reflections", e);
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Get all shared reflections for the user's household.
   * Automatically determines the user's household from their profile.
   *
   * @param principal the authenticated user
   * @param pageable pagination information
   * @return a page of shared reflections
   */
  @GetMapping("/household")
  public ResponseEntity<Page<ReflectionResponseDto>> getHouseholdReflections(
      Principal principal,
      Pageable pageable) {
    try {
      User user = userService.getUserByEmail(principal.getName())
          .orElseThrow(() -> new IllegalStateException("User not found"));

      // Check if the user has a household
      if (user.getHousehold() == null) {
        return ResponseEntity.status(400).body(null); // Bad request if user has no household
      }

      Integer householdId = user.getHousehold().getId();
      Page<ReflectionResponseDto> reflections = reflectionService.getSharedReflectionsByHouseholdId(householdId, pageable);
      return ResponseEntity.ok(reflections);
    } catch (Exception e) {
      log.error("Error getting household reflections", e);
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Get all shared reflections from all groups the user's household is a member of.
   * No need to specify a group ID - automatically retrieves from all user's groups.
   *
   * @param principal the authenticated user
   * @param pageable pagination information
   * @return a page of shared reflections from all groups
   */
  @GetMapping("/groups")
  public ResponseEntity<Page<ReflectionResponseDto>> getGroupReflections(
      Principal principal,
      Pageable pageable) {
    try {
      String email = principal.getName();
      // Verify user exists
      userService.getUserByEmail(email)
          .orElseThrow(() -> new IllegalStateException("User not found"));

      Page<ReflectionResponseDto> reflections = reflectionService.getSharedReflectionsFromAllUserGroups(email, pageable);
      return ResponseEntity.ok(reflections);
    } catch (Exception e) {
      log.error("Error getting reflections from all groups", e);
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Create a new reflection.
   *
   * @param createReflectionDto the reflection data
   * @param principal the authenticated user
   * @return the created reflection
   */
  @PostMapping
  public ResponseEntity<ReflectionResponseDto> createReflection(
      @Valid @RequestBody CreateReflectionDto createReflectionDto,
      Principal principal) {
    try {
      User user = userService.getUserByEmail(principal.getName())
          .orElseThrow(() -> new IllegalStateException("User not found"));
      ReflectionResponseDto reflection = reflectionService.createReflection(user.getId(), createReflectionDto);
      return ResponseEntity.ok(reflection);
    } catch (Exception e) {
      log.error("Error creating reflection", e);
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Update an existing reflection.
   * Only the owner of the reflection can update it.
   *
   * @param id the ID of the reflection to update
   * @param updateReflectionDto the updated reflection data
   * @param principal the authenticated user
   * @return the updated reflection
   */
  @PutMapping("/{id}")
  public ResponseEntity<ReflectionResponseDto> updateReflection(
      @PathVariable Integer id,
      @Valid @RequestBody UpdateReflectionDto updateReflectionDto,
      Principal principal) {
    try {
      User user = userService.getUserByEmail(principal.getName())
          .orElseThrow(() -> new IllegalStateException("User not found"));
      ReflectionResponseDto reflection = reflectionService.updateReflection(id, user.getId(), updateReflectionDto);
      return ResponseEntity.ok(reflection);
    } catch (IllegalArgumentException e) {
      log.error("Error updating reflection", e);
      return ResponseEntity.status(403).build();
    } catch (Exception e) {
      log.error("Error updating reflection", e);
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Soft delete a reflection (mark as deleted).
   * Only the owner of the reflection can delete it.
   *
   * @param id the ID of the reflection to delete
   * @param principal the authenticated user
   * @return 200 OK if successful, 403 Forbidden if not authorized, 400 Bad Request if error
   */
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
      log.error("Error deleting reflection", e);
      return ResponseEntity.status(403).build();
    } catch (Exception e) {
      log.error("Error deleting reflection", e);
      return ResponseEntity.badRequest().build();
    }
  }
}
