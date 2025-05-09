package stud.ntnu.backend.controller.crisis;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import stud.ntnu.backend.dto.map.CreateCrisisEventDto;
import stud.ntnu.backend.dto.map.CrisisEventChangeDto;
import stud.ntnu.backend.dto.map.CrisisEventPreviewDto;
import stud.ntnu.backend.dto.map.UpdateCrisisEventDto;
import stud.ntnu.backend.model.map.CrisisEvent;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.security.AdminChecker;
import stud.ntnu.backend.service.crisis.CrisisEventService;
import stud.ntnu.backend.service.user.UserService;
import org.springframework.data.web.PageableDefault;

/**
 * REST controller for managing crisis events and live updates. Provides endpoints for
 * administrators to create, edit, and deactivate crisis events, as well as endpoints for users to
 * view crisis events affecting them.
 * <p>
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Crisis Events", description = "Operations related to crisis event management")
public class CrisisEventController {

  private final CrisisEventService crisisEventService;
  private final UserService userService;

  /**
   * Constructs a new CrisisEventController with required services.
   *
   * @param crisisEventService service for managing crisis events
   * @param userService        service for managing users
   */
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
  @Operation(summary = "Create crisis event", description = "Creates a new crisis event. Only users with ADMIN or SUPERADMIN roles are allowed to create crisis events.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Crisis event created successfully"),
      @ApiResponse(responseCode = "403", description = "Access forbidden - only administrators can create crisis events", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid crisis event data", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @PostMapping(path = "/admin/crisis-events")
  public ResponseEntity<?> createCrisisEvent(
      @Valid @RequestBody CreateCrisisEventDto createCrisisEventDto,
      Principal principal) {
    try {
      if (!AdminChecker.isCurrentUserAdmin(principal, userService)) {
        return ResponseEntity.status(403).body("Only administrators can create crisis events");
      }

      String email = principal.getName();
      User currentUser = userService.getUserByEmail(email)
          .orElseThrow(() -> new IllegalStateException("User not found"));

      crisisEventService.createCrisisEvent(createCrisisEventDto, currentUser);
      return ResponseEntity.ok().build();
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
   * @return ResponseEntity with status 200 OK if successful, 403 Forbidden if unauthorized, or 404
   * Not Found if the crisis event doesn't exist
   */
  @Operation(summary = "Update crisis event", description = "Updates an existing crisis event. Only users with ADMIN or SUPERADMIN roles are allowed to update crisis events.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Crisis event updated successfully"),
      @ApiResponse(responseCode = "403", description = "Access forbidden - only administrators can update crisis events", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "404", description = "Crisis event not found"),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid update data", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @PutMapping(path = "/admin/crisis-events/{id}")
  public ResponseEntity<?> updateCrisisEvent(
      @PathVariable Integer id,
      @RequestBody UpdateCrisisEventDto updateCrisisEventDto,
      Principal principal) {
    try {
      if (!AdminChecker.isCurrentUserAdmin(principal, userService)) {
        return ResponseEntity.status(403).body("Only administrators can update crisis events");
      }

      CrisisEvent updatedCrisisEvent = crisisEventService.updateCrisisEvent(id,
          updateCrisisEventDto);
      if (updatedCrisisEvent == null) {
        return ResponseEntity.notFound().build();
      }

      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Gets a preview of all active crisis events with pagination. The preview includes only id, name,
   * severity, and startTime.
   *
   * @param pageable the pagination information
   * @return ResponseEntity containing a page of crisis event previews
   */
  @Operation(summary = "Get all crisis event previews", description = "Gets a preview of all active crisis events with pagination. The preview includes only id, name, severity, and startTime.")
  @ApiResponse(responseCode = "200", description = "Successfully retrieved crisis event previews", 
      content = @Content(schema = @Schema(implementation = CrisisEventPreviewDto.class)))
  @GetMapping("/public/crisis-events/all/previews")
  public ResponseEntity<Page<CrisisEventPreviewDto>> getAllCrisisEventPreviews(Pageable pageable) {
    Page<CrisisEventPreviewDto> crisisEventPreviews = crisisEventService.getAllCrisisEventPreviews(
        pageable);
    return ResponseEntity.ok(crisisEventPreviews);
  }

  /**
   * Gets a preview of all inactive crisis events with pagination. The preview includes only id,
   * name, severity, and startTime.
   *
   * @param pageable the pagination information
   * @return ResponseEntity containing a page of crisis event previews
   */
  @Operation(summary = "Get inactive crisis event previews", description = "Gets a preview of all inactive crisis events with pagination. The preview includes only id, name, severity, and startTime.")
  @ApiResponse(responseCode = "200", description = "Successfully retrieved inactive crisis event previews", 
      content = @Content(schema = @Schema(implementation = CrisisEventPreviewDto.class)))
  @GetMapping("/public/crisis-events/inactive/previews")
  public ResponseEntity<Page<CrisisEventPreviewDto>> getInactiveCrisisEventPreviews(
      Pageable pageable) {
    Page<CrisisEventPreviewDto> crisisEventPreviews = crisisEventService.getInactiveCrisisEventPreviews(
        pageable);
    return ResponseEntity.ok(crisisEventPreviews);
  }

  /**
   * Gets a page of all crisis events with pagination. Returns the full entity, not just a preview.
   *
   * @param pageable the pagination information
   * @return ResponseEntity containing a page of crisis events
   */
  @Operation(summary = "Get all crisis events", description = "Gets a page of all crisis events with pagination. Returns the full entity, not just a preview.")
  @ApiResponse(responseCode = "200", description = "Successfully retrieved crisis events", 
      content = @Content(schema = @Schema(implementation = CrisisEvent.class)))
  @GetMapping("/public/crisis-events/all")
  public ResponseEntity<Page<CrisisEvent>> getAllCrisisEvents(Pageable pageable) {
    Page<CrisisEvent> crisisEvents = crisisEventService.getAllCrisisEvents(pageable);
    return ResponseEntity.ok(crisisEvents);
  }

  /**
   * Deactivates a crisis event by its ID. Only users with ADMIN or SUPERADMIN roles are allowed to
   * deactivate crisis events.
   *
   * @param id        the ID of the crisis event to deactivate
   * @param principal the Principal object representing the current user
   * @return ResponseEntity with status 200 OK if successful, or 403 Forbidden if unauthorized
   */
  @Operation(summary = "Deactivate crisis event", description = "Deactivates a crisis event by its ID. Only users with ADMIN or SUPERADMIN roles are allowed to deactivate crisis events.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Crisis event deactivated successfully", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "403", description = "Access forbidden - only administrators can deactivate crisis events", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "400", description = "Bad request - operation failed", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @PutMapping(path = "/admin/crisis-events/deactivate/{id}")
  public ResponseEntity<?> deactivateCrisisEvent(
      @PathVariable Integer id,
      Principal principal) {
    try {
      if (!AdminChecker.isCurrentUserAdmin(principal, userService)) {
        return ResponseEntity.status(403).body("Only administrators can deactivate crisis events");
      }

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
   * @return ResponseEntity containing the crisis event if found, or 404 Not Found if not found
   */
  @Operation(summary = "Get crisis event by ID", description = "Gets a specific crisis event by its ID.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved crisis event", 
          content = @Content(schema = @Schema(implementation = CrisisEvent.class))),
      @ApiResponse(responseCode = "404", description = "Crisis event not found"),
      @ApiResponse(responseCode = "400", description = "Bad request - operation failed", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @GetMapping("/public/crisis-events/{id}")
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
   * @return ResponseEntity containing a page of crisis event changes if successful, or 404 Not
   * Found if the crisis event doesn't exist
   */
  @Operation(summary = "Get crisis event changes", description = "Gets all changes for a specific crisis event with pagination.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved crisis event changes", 
          content = @Content(schema = @Schema(implementation = CrisisEventChangeDto.class))),
      @ApiResponse(responseCode = "404", description = "Crisis event not found"),
      @ApiResponse(responseCode = "400", description = "Bad request - operation failed", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @GetMapping(path = "/public/crisis-events/{id}/changes")
  public ResponseEntity<?> getCrisisEventChanges(
      @PathVariable Integer id,
      Pageable pageable,
      Principal principal) {
    try {
      Page<CrisisEventChangeDto> changes = crisisEventService.getCrisisEventChanges(id, pageable);
      return ResponseEntity.ok(changes);
    } catch (IllegalStateException e) {
      return ResponseEntity.notFound().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Gets a paginated list of crisis events affecting the current user. A crisis event affects a
   * user if the user's home or household location is within the event's radius.
   *
   * @param principal the Principal object representing the current user
   * @param pageable  the pagination information
   * @return ResponseEntity with a page of crisis events affecting the user
   */
  @Operation(summary = "Get crisis events affecting current user", description = "Gets a paginated list of crisis events affecting the current user. A crisis event affects a user if the user's home or household location is within the event's radius.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved affecting crisis events", 
          content = @Content(schema = @Schema(implementation = CrisisEvent.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - operation failed")
  })
  @GetMapping("/user/crisis-events/current-user")
  public ResponseEntity<Page<CrisisEvent>> getCrisisEventsAffectingCurrentUser(
      Principal principal,
      Pageable pageable) {
    try {
      String email = principal.getName();
      User currentUser = userService.getUserByEmail(email)
          .orElseThrow(() -> new IllegalStateException("User not found"));
      Page<CrisisEvent> events = crisisEventService.getCrisisEventsAffectingUser(currentUser,
          pageable);
      return ResponseEntity.ok(events);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Gets a paginated list of crisis event previews affecting the current user, sorted by severity
   * (red > yellow > green).
   *
   * @param principal the Principal object representing the current user
   * @param pageable  the pagination information
   * @return ResponseEntity with a page of crisis event previews affecting the user
   */
  @Operation(summary = "Get crisis event previews affecting current user", description = "Gets a paginated list of crisis event previews affecting the current user, sorted by severity (red > yellow > green).")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved affecting crisis event previews", 
          content = @Content(schema = @Schema(implementation = CrisisEventPreviewDto.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - operation failed")
  })
  @GetMapping("/user/crisis-events/all/current-user")
  public ResponseEntity<Page<CrisisEventPreviewDto>> getAllCrisisEventPreviewsAffectingUser(
      Principal principal,
      Pageable pageable) {
    try {
      String email = principal.getName();
      User currentUser = userService.getUserByEmail(email)
          .orElseThrow(() -> new IllegalStateException("User not found"));
      Page<CrisisEventPreviewDto> page = crisisEventService.getCrisisEventPreviewsAffectingUserSortedBySeverity(
          currentUser, pageable);
      return ResponseEntity.ok(page);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Search for crisis events by name.
   *
   * @param nameSearch the search term to filter event names by
   * @param isActive   whether to search among active (true) or inactive (false) events
   * @param pageable   pagination information
   * @return page of crisis events matching the name search
   */
  @Operation(summary = "Search crisis events", description = "Search for crisis events by name with optional filtering by active status.")
  @ApiResponse(responseCode = "200", description = "Successfully retrieved matching crisis events", 
      content = @Content(schema = @Schema(implementation = CrisisEventPreviewDto.class)))
  @GetMapping("/public/crisis-events/search")
  public ResponseEntity<Page<CrisisEventPreviewDto>> searchCrisisEventsByName(
      @RequestParam(required = false) String nameSearch,
      @RequestParam(required = false, defaultValue = "true") boolean isActive,
      @PageableDefault(size = 10, sort = "startTime", direction = Sort.Direction.DESC) Pageable pageable) {

    Page<CrisisEvent> events = crisisEventService.searchCrisisEvents(nameSearch, isActive,
        pageable);
    Page<CrisisEventPreviewDto> previewDtos = events.map(CrisisEventPreviewDto::fromEntity);
    return ResponseEntity.ok(previewDtos);
  }

  private int severityOrder(CrisisEvent.Severity severity) {
    return switch (severity) {
      case red -> 3;
      case yellow -> 2;
      case green -> 1;
    };
  }


}
