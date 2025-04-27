package stud.ntnu.backend.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import java.security.Principal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.backend.dto.map.CreateCrisisEventDto;
import stud.ntnu.backend.dto.map.UpdateCrisisEventDto;
import stud.ntnu.backend.model.map.CrisisEvent;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.security.AdminChecker;
import stud.ntnu.backend.service.CrisisEventService;
import stud.ntnu.backend.service.UserService;

import java.time.LocalDateTime;

/**
 * Manages crisis events and live updates. Admin functions include creating, editing, and deleting
 * events, setting severity levels, and defining epicenter locations. Also exposes current events
 * for users.
 * <p>
 * Based on Visjonsdokument 2025 for Krisefikser.no.
 */
@RestController
@RequestMapping("/api/crisis-events")
public class CrisisEventController {

  private final CrisisEventService crisisEventService;
  private final UserService userService;

  public CrisisEventController(CrisisEventService crisisEventService, UserService userService) {
    this.crisisEventService = crisisEventService;
    this.userService = userService;
  }

  /**
   * Creates a new crisis event. Only users with ADMIN or SUPERADMIN roles are allowed to create
   * crisis events. The start time must be provided in the request and cannot be changed after creation.
   *
   * @param createCrisisEventDto the crisis event information including the required start time
   * @param principal the Principal object representing the current user
   * @return ResponseEntity with status 200 OK if successful, or 403 Forbidden if unauthorized
   */
  @PostMapping
  public ResponseEntity<?> createCrisisEvent(
      @Valid @RequestBody CreateCrisisEventDto createCrisisEventDto,
      Principal principal) {
    try {
      // Check if the current user is an admin using AdminChecker with Principal
      if (!AdminChecker.isCurrentUserAdmin(principal, userService)) {
        return ResponseEntity.status(403).body("Only administrators can create crisis events");
      }

      // Get the current authenticated user using Principal
      String email = principal.getName();
      User currentUser = userService.getUserByEmail(email)
          .orElseThrow(() -> new IllegalStateException("User not found"));

      // Delegate to service for creating the crisis event
      CrisisEvent savedCrisisEvent = crisisEventService.createCrisisEvent(createCrisisEventDto, currentUser);

      return ResponseEntity.ok(savedCrisisEvent);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Updates an existing crisis event. Only users with ADMIN or SUPERADMIN roles are allowed to
   * update crisis events. If a field is not provided in the request, it will not be updated.
   * Note: The start time of a crisis event cannot be updated after creation.
   *
   * @param id                   the ID of the crisis event to update
   * @param updateCrisisEventDto the crisis event information to update (excluding start time)
   * @param principal            the Principal object representing the current user
   * @return ResponseEntity with the updated crisis event if successful, or an error message if the
   * crisis event is not found or the user is not authorized
   */
  @PutMapping("/{id}")
  public ResponseEntity<?> updateCrisisEvent(
      @PathVariable Integer id,
      @RequestBody UpdateCrisisEventDto updateCrisisEventDto,
      Principal principal) {
    try {
      // Check if the current user is an admin using AdminChecker with Principal
      if (!AdminChecker.isCurrentUserAdmin(principal, userService)) {
        return ResponseEntity.status(403).body("Only administrators can update crisis events");
      }

      // Delegate to service for updating the crisis event
      CrisisEvent updatedCrisisEvent = crisisEventService.updateCrisisEvent(id, updateCrisisEventDto);

      return ResponseEntity.ok(updatedCrisisEvent);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Gets all crisis events with pagination.
   * TODO: Untested!
   * @param pageable the pagination information
   * @return ResponseEntity with a page of crisis events
   */
  @GetMapping("/all")
  public ResponseEntity<Page<CrisisEvent>> getAllCrisisEvents(Pageable pageable) {
    Page<CrisisEvent> crisisEvents = crisisEventService.getAllCrisisEvents(pageable);
    return ResponseEntity.ok(crisisEvents);
  }
}
