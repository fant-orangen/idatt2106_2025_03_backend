package stud.ntnu.backend.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import stud.ntnu.backend.dto.map.CreateCrisisEventDto;
import stud.ntnu.backend.dto.map.CrisisEventChangeDto;
import stud.ntnu.backend.dto.map.CrisisEventPreviewDto;
import stud.ntnu.backend.dto.map.UpdateCrisisEventDto;
import stud.ntnu.backend.model.map.CrisisEvent;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.security.AdminChecker;
import stud.ntnu.backend.service.CrisisEventService;
import stud.ntnu.backend.service.UserService;

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
  private final Logger log = LoggerFactory.getLogger(CrisisEventController.class);

  public CrisisEventController(CrisisEventService crisisEventService, UserService userService) {
    this.crisisEventService = crisisEventService;
    this.userService = userService;
  }

  /**
   * Creates a new crisis event. Only users with ADMIN or SUPERADMIN roles are allowed to create
   * crisis events. The start time must be provided in the request and cannot be changed after
   * creation.
   *
   * @param createCrisisEventDto the crisis event information including the required start time
   * @param principal            the Principal object representing the current user
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
      CrisisEvent savedCrisisEvent = crisisEventService.createCrisisEvent(createCrisisEventDto,
          currentUser);

      return ResponseEntity.ok(savedCrisisEvent);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Updates an existing crisis event. Only users with ADMIN or SUPERADMIN roles are allowed to
   * update crisis events. If a field is not provided in the request, it will not be updated. Note:
   * The start time of a crisis event cannot be updated after creation.
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
      CrisisEvent updatedCrisisEvent = crisisEventService.updateCrisisEvent(id,
          updateCrisisEventDto);

      if (updatedCrisisEvent == null) {
        return ResponseEntity.notFound().build();
      }

      return ResponseEntity.ok(updatedCrisisEvent);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Gets a preview of all crisis events with pagination (id, name, severity, startTime only).
   *
   * @param pageable the pagination information
   * @return ResponseEntity with a page of crisis event previews
   */
  @GetMapping("/all/previews")
  public ResponseEntity<Page<CrisisEventPreviewDto>> getAllCrisisEventPreviews(Pageable pageable) {
    Page<CrisisEventPreviewDto> crisisEventPreviews = crisisEventService.getAllCrisisEventPreviews(pageable);
    return ResponseEntity.ok(crisisEventPreviews);
  }
  
  /**
   * Gets a page of all crisis events (full entity, not preview) with pagination.
   *
   * @param pageable the pagination information
   * @return ResponseEntity with a page of crisis events
   */
  @GetMapping("/all")
  public ResponseEntity<Page<CrisisEvent>> getAllCrisisEvents(Pageable pageable) {
    Page<CrisisEvent> crisisEvents = crisisEventService.getAllCrisisEvents(pageable);
    return ResponseEntity.ok(crisisEvents);
  }
  
  /**
   * Deletes a crisis event by its ID. Only users with ADMIN or SUPERADMIN roles are allowed to
   * delete crisis events.
   *
   * @param id        the ID of the crisis event to delete
   * @param principal the Principal object representing the current user
   * @return ResponseEntity with status 200 OK if successful, or 403 Forbidden if unauthorized
   */
  @PutMapping("/deactivate/{id}")
  public ResponseEntity<?> deleteCrisisEvent(
      @PathVariable Integer id,
      Principal principal) {
    try {
      // Check if the current user is an admin using AdminChecker with Principal
      if (!AdminChecker.isCurrentUserAdmin(principal, userService)) {
        return ResponseEntity.status(403).body("Only administrators can deactivate crisis events");
      }

      // Delegate to service for deleting the crisis event
      crisisEventService.deactivateCrisisEvent(id);

      return ResponseEntity.ok("Crisis event deactivated");
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Gets a specific crisis event by its ID.
   *
   * @param id the ID of the crisis event to retrieve
   * @return ResponseEntity with the crisis event if found, or 404 Not Found if not found
   */
  @GetMapping("/{id}")
  public ResponseEntity<?> getCrisisEventById(@PathVariable Integer id) {
    try {
      return crisisEventService.getCrisisEventDetailsById(id)
          .map(ResponseEntity::ok)
          .orElse(ResponseEntity.notFound().build());
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Gets all changes for a specific crisis event with pagination.
   *
   * @param id        the ID of the crisis event
   * @param pageable  the pagination information
   * @param principal the Principal object representing the current user
   * @return ResponseEntity with a page of crisis event changes if successful, or an error message
   * if the crisis event is not found or the user is not authorized
   */
  @GetMapping("/{id}/changes")
  public ResponseEntity<?> getCrisisEventChanges(
      @PathVariable Integer id,
      Pageable pageable,
      Principal principal) {
    try {

      // Get the crisis event changes
      Page<CrisisEventChangeDto> changes = crisisEventService.getCrisisEventChanges(id, pageable);

      return ResponseEntity.ok(changes);
    } catch (IllegalStateException e) {
      return ResponseEntity.notFound().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Gets a paginated list of crisis events affecting the current user. A crisis event affects a user if the user's home or household location is within the event's radius.
   *
   * @param principal the Principal object representing the current user
   * @param pageable the pagination information
   * @return ResponseEntity with a page of crisis events affecting the user
   */
  @GetMapping("/current-user")
  public ResponseEntity<Page<CrisisEvent>> getCrisisEventsAffectingCurrentUser(
      Principal principal,
      Pageable pageable) {
    try {
      String email = principal.getName();
      User currentUser = userService.getUserByEmail(email)
          .orElseThrow(() -> new IllegalStateException("User not found"));
      Page<CrisisEvent> events = crisisEventService.getCrisisEventsAffectingUser(currentUser, pageable);
      return ResponseEntity.ok(events);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Gets a paginated list of crisis event previews affecting the current user, sorted by severity (red > yellow > green).
   *
   * @param principal the Principal object representing the current user
   * @param pageable the pagination information
   * @return ResponseEntity with a page of crisis event previews affecting the user
   */
  @GetMapping("/all/current-user")
  public ResponseEntity<Page<CrisisEventPreviewDto>> getAllCrisisEventPreviewsAffectingUser(
      Principal principal,
      Pageable pageable) {
    try {
      String email = principal.getName();
      User currentUser = userService.getUserByEmail(email)
          .orElseThrow(() -> new IllegalStateException("User not found"));
      Page<CrisisEventPreviewDto> page = crisisEventService.getCrisisEventPreviewsAffectingUserSortedBySeverity(currentUser, pageable);
      return ResponseEntity.ok(page);
    } catch (Exception e) {
      log.info("Error getting crisis event previews affecting user: {}", e.getMessage());
      return ResponseEntity.badRequest().build();
    }
  }

  private int severityOrder(CrisisEvent.Severity severity) {
    return switch (severity) {
      case red -> 3;
      case yellow -> 2;
      case green -> 1;
    };
  }
}
