package stud.ntnu.backend.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.backend.dto.map.CreateCrisisEventDto;
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
   * crisis events.
   *
   * @param createCrisisEventDto the crisis event information
   * @return ResponseEntity with status 200 OK if successful, or 403 Forbidden if unauthorized
   */
  @PostMapping
  public ResponseEntity<?> createCrisisEvent(
      @Valid @RequestBody CreateCrisisEventDto createCrisisEventDto) {
    try {
      // Check if the current user is an admin using AdminChecker
      if (!AdminChecker.isCurrentUserAdmin(userService)) {
        return ResponseEntity.status(403).body("Only administrators can create crisis events");
      }

      // Get the current authenticated user
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      String email = authentication.getName();
      User currentUser = userService.getUserByEmail(email)
          .orElseThrow(() -> new IllegalStateException("User not found"));

      // Create a new crisis event
      CrisisEvent crisisEvent = new CrisisEvent(
          createCrisisEventDto.getName(),
          createCrisisEventDto.getLatitude(),
          createCrisisEventDto.getLongitude(),
          LocalDateTime.now(),
          currentUser
      );

      // Set optional fields
      crisisEvent.setDescription(createCrisisEventDto.getDescription());
      crisisEvent.setSeverity(createCrisisEventDto.getSeverity());

      // Save the crisis event
      CrisisEvent savedCrisisEvent = crisisEventService.saveCrisisEvent(crisisEvent);

      return ResponseEntity.ok(savedCrisisEvent);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}
