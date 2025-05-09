package stud.ntnu.backend.controller.map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import stud.ntnu.backend.dto.map.CreateMeetingPlaceDto;
import stud.ntnu.backend.dto.map.MeetingPlaceDto;
import stud.ntnu.backend.dto.map.MeetingPlacePreviewDto;
import stud.ntnu.backend.model.map.MeetingPlace;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.security.AdminChecker;
import stud.ntnu.backend.service.map.MeetingPlaceService;
import stud.ntnu.backend.service.user.UserService;

/**
 * REST controller for managing meeting places in the crisis coordination system.
 * <p>
 * This controller provides endpoints for:
 * <ul>
 *   <li>Creating new meeting places (admin only)</li>
 *   <li>Archiving and activating meeting places (admin only)</li>
 *   <li>Retrieving meeting places by location, ID, or paginated lists</li>
 *   <li>Managing meeting place previews</li>
 * </ul>
 * <p>
 * All endpoints are secured and require appropriate authentication.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Meeting Places", description = "Operations for managing meeting places in the crisis coordination system")
public class MeetingPlaceController {

  private final MeetingPlaceService meetingPlaceService;
  private final UserService userService;

  /**
   * Constructs a new MeetingPlaceController with the required services.
   *
   * @param meetingPlaceService service for managing meeting places
   * @param userService         service for user operations
   */
  public MeetingPlaceController(MeetingPlaceService meetingPlaceService, UserService userService) {
    this.meetingPlaceService = meetingPlaceService;
    this.userService = userService;
  }

  /**
   * Creates a new meeting place. Only accessible by administrators.
   * <p>
   * Note: Address to coordinates conversion is not yet implemented.
   *
   * @param createDto the DTO containing meeting place information
   * @param principal the authenticated user making the request
   * @return ResponseEntity containing: - 200 OK with the created meeting place if successful - 403
   * Forbidden if user is not an admin - 400 Bad Request if creation fails
   */
  @Operation(summary = "Create meeting place", description = "Creates a new meeting place. Only accessible by administrators.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully created meeting place", 
          content = @Content(schema = @Schema(implementation = MeetingPlaceDto.class))),
      @ApiResponse(responseCode = "403", description = "Forbidden - user is not an admin", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid data", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @PostMapping("/admin/meeting-places")
  public ResponseEntity<?> createMeetingPlace(
      @Valid @RequestBody CreateMeetingPlaceDto createDto,
      Principal principal) {
    try {
      if (!AdminChecker.isCurrentUserAdmin(principal, userService)) {
        return ResponseEntity.status(403).body("Only administrators can create meeting places");
      }

      User currentUser = userService.getUserByEmail(principal.getName())
          .orElseThrow(() -> new IllegalStateException("User not found"));

      MeetingPlace savedPlace = meetingPlaceService.createMeetingPlace(createDto, currentUser);
      return ResponseEntity.ok(MeetingPlaceDto.fromEntity(savedPlace));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Archives a meeting place. Only accessible by administrators.
   *
   * @param id        the ID of the meeting place to archive
   * @param principal the authenticated user making the request
   * @return ResponseEntity containing: - 200 OK with the archived meeting place if successful - 403
   * Forbidden if user is not an admin - 400 Bad Request if archiving fails
   */
  @Operation(summary = "Archive meeting place", description = "Archives a meeting place. Only accessible by administrators.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully archived meeting place", 
          content = @Content(schema = @Schema(implementation = MeetingPlaceDto.class))),
      @ApiResponse(responseCode = "403", description = "Forbidden - user is not an admin", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid meeting place ID", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @PatchMapping("/admin/meeting-places/{id}/archive")
  public ResponseEntity<?> archiveMeetingPlace(
      @PathVariable Integer id,
      Principal principal) {
    try {
      if (!AdminChecker.isCurrentUserAdmin(principal, userService)) {
        return ResponseEntity.status(403).body("Only administrators can archive meeting places");
      }

      MeetingPlace archivedPlace = meetingPlaceService.archiveMeetingPlace(id);
      return ResponseEntity.ok(MeetingPlaceDto.fromEntity(archivedPlace));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Activates a meeting place. Only accessible by administrators.
   *
   * @param id        the ID of the meeting place to activate
   * @param principal the authenticated user making the request
   * @return ResponseEntity containing: - 200 OK with the activated meeting place if successful -
   * 403 Forbidden if user is not an admin - 400 Bad Request if activation fails
   */
  @Operation(summary = "Activate meeting place", description = "Activates a meeting place. Only accessible by administrators.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully activated meeting place", 
          content = @Content(schema = @Schema(implementation = MeetingPlaceDto.class))),
      @ApiResponse(responseCode = "403", description = "Forbidden - user is not an admin", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid meeting place ID", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @PatchMapping("/admin/meeting-places/{id}/activate")
  public ResponseEntity<?> activateMeetingPlace(
      @PathVariable Integer id,
      Principal principal) {
    try {
      if (!AdminChecker.isCurrentUserAdmin(principal, userService)) {
        return ResponseEntity.status(403).body("Only administrators can activate meeting places");
      }

      MeetingPlace activatedPlace = meetingPlaceService.activateMeetingPlace(id);
      return ResponseEntity.ok(MeetingPlaceDto.fromEntity(activatedPlace));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Retrieves all active meeting places within a specified distance of a location.
   *
   * @param latitude   the latitude of the center point
   * @param longitude  the longitude of the center point
   * @param distanceKm the search radius in kilometers (defaults to 10.0)
   * @return ResponseEntity containing: - 200 OK with list of nearby meeting places if successful -
   * 400 Bad Request if retrieval fails
   */
  @Operation(summary = "Get nearby meeting places", description = "Retrieves all active meeting places within a specified distance of a location.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved nearby meeting places", 
          content = @Content(schema = @Schema(implementation = MeetingPlaceDto.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid coordinates")
  })
  @GetMapping("/public/meeting-places/nearby")
  public ResponseEntity<List<MeetingPlaceDto>> getNearbyMeetingPlaces(
      @RequestParam BigDecimal latitude,
      @RequestParam BigDecimal longitude,
      @RequestParam(required = false, defaultValue = "10.0") double distanceKm) {
    try {
      List<MeetingPlace> nearbyPlaces = meetingPlaceService.getNearbyMeetingPlaces(
          latitude, longitude, distanceKm);
      List<MeetingPlaceDto> dtos = nearbyPlaces.stream()
          .map(MeetingPlaceDto::fromEntity)
          .toList();
      return ResponseEntity.ok(dtos);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Retrieves a paginated list of all meeting places.
   *
   * @param page the page number (0-based, defaults to 0)
   * @param size the number of items per page (defaults to 10)
   * @return ResponseEntity containing: - 200 OK with paginated list of meeting places if successful
   * - 400 Bad Request if retrieval fails
   */
  @Operation(summary = "Get all meeting places", description = "Retrieves a paginated list of all meeting places.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved meeting places", 
          content = @Content(schema = @Schema(implementation = MeetingPlaceDto.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid pagination parameters")
  })
  @GetMapping("/public/meeting-places/all")
  public ResponseEntity<Page<MeetingPlaceDto>> getAllMeetingPlaces(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Page<MeetingPlace> meetingPlaces = meetingPlaceService.getAllMeetingPlacesPaginated(page,
          size);
      Page<MeetingPlaceDto> dtos = meetingPlaces.map(MeetingPlaceDto::fromEntity);
      return ResponseEntity.ok(dtos);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Retrieves a paginated list of meeting place previews.
   *
   * @param page the page number (0-based, defaults to 0)
   * @param size the number of items per page (defaults to 10)
   * @return ResponseEntity containing: - 200 OK with paginated list of meeting place previews if
   * successful - 400 Bad Request if retrieval fails
   */
  @Operation(summary = "Get meeting place previews", description = "Retrieves a paginated list of meeting place previews.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved meeting place previews", 
          content = @Content(schema = @Schema(implementation = MeetingPlacePreviewDto.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid pagination parameters")
  })
  @GetMapping("/public/meeting-places/all/previews")
  public ResponseEntity<Page<MeetingPlacePreviewDto>> getAllMeetingPlacePreviews(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Page<MeetingPlacePreviewDto> previews = meetingPlaceService.getAllMeetingPlacePreviewsPaginated(
          page, size);
      return ResponseEntity.ok(previews);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Retrieves a specific meeting place by its ID.
   *
   * @param id the ID of the meeting place to retrieve
   * @return ResponseEntity containing: - 200 OK with the meeting place if found - 404 Not Found if
   * the meeting place doesn't exist - 400 Bad Request if retrieval fails
   */
  @Operation(summary = "Get meeting place by ID", description = "Retrieves a specific meeting place by its ID.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved meeting place", 
          content = @Content(schema = @Schema(implementation = MeetingPlaceDto.class))),
      @ApiResponse(responseCode = "404", description = "Meeting place not found"),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid meeting place ID")
  })
  @GetMapping("/public/meeting-places/{id}")
  public ResponseEntity<MeetingPlaceDto> getMeetingPlaceById(@PathVariable Integer id) {
    try {
      return meetingPlaceService.getMeetingPlaceById(id)
          .map(MeetingPlaceDto::fromEntity)
          .map(ResponseEntity::ok)
          .orElse(ResponseEntity.notFound().build());
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }
}
