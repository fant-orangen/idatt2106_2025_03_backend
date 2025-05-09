package stud.ntnu.backend.controller.map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import stud.ntnu.backend.dto.poi.*;
import stud.ntnu.backend.model.map.PointOfInterest;
import stud.ntnu.backend.model.map.PoiType;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.security.AdminChecker;
import stud.ntnu.backend.service.map.PoiService;
import stud.ntnu.backend.service.user.UserService;
import stud.ntnu.backend.util.LocationUtil;

/**
 * Controller responsible for managing Points of Interest (POIs) operations. Provides endpoints for
 * creating, reading, updating, and deleting POIs, as well as searching and filtering POIs based on
 * various criteria. POIs include resources like emergency shelters, defibrillators, and food
 * stations.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Points of Interest", description = "Operations for managing Points of Interest (POIs) including emergency shelters, defibrillators, and food stations")
public class PoiController {

  private final PoiService poiService;
  private final UserService userService;

  /**
   * Constructs a new PoiController with the required services.
   *
   * @param poiService  service handling POI-related operations
   * @param userService service handling user-related operations
   */
  public PoiController(PoiService poiService, UserService userService) {
    this.poiService = poiService;
    this.userService = userService;
  }

  /**
   * Retrieves all public points of interest. This endpoint is accessible without authentication.
   *
   * @return List of POIs converted to DTOs for public consumption
   */
  @Operation(summary = "Get public POIs", description = "Retrieves all public points of interest. This endpoint is accessible without authentication.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved public POIs", 
          content = @Content(schema = @Schema(implementation = PoiItemDto.class)))
  })
  @GetMapping("/public/poi/public")
  public List<PoiItemDto> getPublicPointsOfInterest() {
    return poiService.getAllPointsOfInterest()
        .stream()
        .map(PoiItemDto::fromEntity)
        .toList();
  }

  /**
   * Retrieves a paginated and sorted list of POI previews. This endpoint is accessible without
   * authentication.
   *
   * @param page zero-based page index (default 0)
   * @param size number of items per page (default 10)
   * @param sort sorting criteria in format "property,direction" (default "id,asc")
   * @return ResponseEntity containing a page of POI previews
   */
  @Operation(summary = "Get POI previews", description = "Retrieves a paginated and sorted list of POI previews. This endpoint is accessible without authentication.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved POI previews", 
          content = @Content(schema = @Schema(implementation = PoiItemDto.class)))
  })
  @GetMapping("public/poi/previews")
  public ResponseEntity<?> getPoiPreviews(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "id,asc") String sort) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(sort.split(",")[0]).ascending());
    if (sort.endsWith(",desc")) {
      pageable = PageRequest.of(page, size, Sort.by(sort.split(",")[0]).descending());
    }
    return ResponseEntity.ok(poiService.getPoiPreviews(pageable));
  }

  /**
   * Retrieves all POIs of a specific type. This endpoint is accessible without authentication.
   *
   * @param id the type ID to filter POIs by
   * @return List of POIs of the specified type
   * @throws IllegalArgumentException if the type ID is invalid
   */
  @Operation(summary = "Get POIs by type", description = "Retrieves all POIs of a specific type. This endpoint is accessible without authentication.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved POIs by type", 
          content = @Content(schema = @Schema(implementation = PoiItemDto.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid type ID", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @GetMapping("/public/poi/type/{id}")
  public List<PoiItemDto> getPointsOfInterestByTypeId(@PathVariable int id) {
    return poiService.getPointsOfInterestByTypeId(id)
        .stream()
        .map(PoiItemDto::fromEntity)
        .toList();
  }

  /**
   * Retrieves a specific POI by its ID. This endpoint is accessible without authentication.
   *
   * @param id the ID of the POI to retrieve
   * @return the POI if found, null otherwise
   */
  @Operation(summary = "Get POI by ID", description = "Retrieves a specific POI by its ID. This endpoint is accessible without authentication.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved POI", 
          content = @Content(schema = @Schema(implementation = PoiItemDto.class))),
      @ApiResponse(responseCode = "404", description = "POI not found")
  })
  @GetMapping("/public/poi/{id}")
  public PoiItemDto getPointOfInterestById(@PathVariable int id) {
    return poiService.getPointOfInterestById(id)
        .map(PoiItemDto::fromEntity)
        .orElse(null);
  }

  /**
   * Finds POIs within a specified distance from a given location. Optionally filters by POI type.
   * This endpoint is accessible without authentication.
   *
   * @param id        optional type ID to filter POIs by
   * @param latitude  latitude of the center point
   * @param longitude longitude of the center point
   * @param distance  maximum distance in meters from the center point
   * @return List of POIs within the specified distance
   */
  @Operation(summary = "Get nearby POIs", description = "Finds POIs within a specified distance from a given location. Optionally filters by POI type.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved nearby POIs", 
          content = @Content(schema = @Schema(implementation = PoiItemDto.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid coordinates or distance")
  })
  @GetMapping("/public/poi/type/nearby")
  public List<PoiItemDto> getPointsOfInterestByTypeIdAndDistance(
      @RequestParam(required = false) Integer id,
      @RequestParam double latitude,
      @RequestParam double longitude,
      @RequestParam double distance) {
    return (id == null ? poiService.getAllPointsOfInterest()
        : poiService.getPointsOfInterestByTypeId(id))
        .stream()
        .filter(poi -> LocationUtil.calculateDistance(latitude, longitude,
            poi.getLatitude().doubleValue(), poi.getLongitude().doubleValue()) <= distance)
        .map(PoiItemDto::fromEntity)
        .toList();
  }

  /**
   * Finds the nearest POI of a specific type from a given location. This endpoint is accessible
   * without authentication.
   *
   * @param id        type ID to filter POIs by
   * @param latitude  latitude of the reference point
   * @param longitude longitude of the reference point
   * @return the nearest POI of the specified type, or null if none found
   * @throws IllegalArgumentException if the type ID is invalid
   */
  @Operation(summary = "Get nearest POI", description = "Finds the nearest POI of a specific type from a given location.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved nearest POI", 
          content = @Content(schema = @Schema(implementation = PoiItemDto.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid type ID or coordinates", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @GetMapping("/public/poi/type/nearest/{id}")
  public PoiItemDto getNearestPointOfInterestByType(
      @PathVariable int id,
      @RequestParam double latitude,
      @RequestParam double longitude) {
    PointOfInterest nearestPoi = PoiService.findNearestPoi(latitude, longitude,
        poiService.getPointsOfInterestByTypeId(id));
    return nearestPoi != null ? PoiItemDto.fromEntity(nearestPoi) : null;
  }

  /**
   * Creates a new POI. This endpoint is restricted to admin users only.
   *
   * @param createPoiDto DTO containing the POI information
   * @param principal    the authenticated user's principal
   * @return ResponseEntity containing the created POI if successful, or an error message
   * @throws IllegalArgumentException if the POI data is invalid
   * @throws IllegalStateException    if the user is not found
   */
  @Operation(summary = "Create POI", description = "Creates a new POI. This endpoint is restricted to admin users only.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully created POI", 
          content = @Content(schema = @Schema(implementation = PoiItemDto.class))),
      @ApiResponse(responseCode = "403", description = "Forbidden - user is not an admin", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid POI data", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @PostMapping("/admin/poi")
  public ResponseEntity<?> createPointOfInterest(
      @Valid @RequestBody CreatePoiDto createPoiDto,
      Principal principal) {
    try {
      if (!AdminChecker.isCurrentUserAdmin(principal, userService)) {
        return ResponseEntity.status(403).body("Only administrators can create points of interest");
      }

      String email = principal.getName();
      User currentUser = userService.getUserByEmail(email)
          .orElseThrow(() -> new IllegalStateException("User not found"));

      PointOfInterest savedPoi = poiService.createPointOfInterest(createPoiDto, currentUser);
      return ResponseEntity.ok(PoiItemDto.fromEntity(savedPoi));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Updates an existing POI. This endpoint is restricted to admin users only.
   *
   * @param id           the ID of the POI to update
   * @param updatePoiDto DTO containing the updated POI information
   * @param principal    the authenticated user's principal
   * @return ResponseEntity containing the updated POI if successful, or an error message
   * @throws IllegalArgumentException if the POI doesn't exist or the data is invalid
   */
  @Operation(summary = "Update POI", description = "Updates an existing POI. This endpoint is restricted to admin users only.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully updated POI", 
          content = @Content(schema = @Schema(implementation = PoiItemDto.class))),
      @ApiResponse(responseCode = "403", description = "Forbidden - user is not an admin", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid POI data", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @PutMapping("/admin/poi/{id}")
  public ResponseEntity<?> updatePointOfInterest(
      @PathVariable Integer id,
      @RequestBody UpdatePoiDto updatePoiDto,
      Principal principal) {
    try {
      if (!AdminChecker.isCurrentUserAdmin(principal, userService)) {
        return ResponseEntity.status(403).body("Only administrators can update points of interest");
      }

      PointOfInterest updatedPoi = poiService.updatePointOfInterest(id, updatePoiDto);
      return ResponseEntity.ok(PoiItemDto.fromEntity(updatedPoi));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Deletes a POI. This endpoint is restricted to admin users only.
   *
   * @param id        the ID of the POI to delete
   * @param principal the authenticated user's principal
   * @return ResponseEntity with success message if successful, or an error message
   * @throws IllegalArgumentException if the POI doesn't exist
   */
  @Operation(summary = "Delete POI", description = "Deletes a POI. This endpoint is restricted to admin users only.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully deleted POI", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "403", description = "Forbidden - user is not an admin", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid POI ID", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @DeleteMapping("/admin/poi/{id}")
  public ResponseEntity<?> deletePointOfInterest(
      @PathVariable Integer id,
      Principal principal) {
    try {
      if (!AdminChecker.isCurrentUserAdmin(principal, userService)) {
        return ResponseEntity.status(403).body("Only administrators can delete points of interest");
      }

      poiService.deletePointOfInterest(id);
      return ResponseEntity.ok("Point of interest deleted successfully");
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Retrieves all available POI types. This endpoint is accessible without authentication.
   *
   * @return List of all POI types in the system
   */
  @Operation(summary = "Get POI types", description = "Retrieves all available POI types. This endpoint is accessible without authentication.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved POI types", 
          content = @Content(schema = @Schema(implementation = PoiType.class)))
  })
  @GetMapping("/public/poi/types")
  public List<PoiType> getAllPoiTypes() {
    return poiService.getAllPoiTypes();
  }

  /**
   * Searches for POIs by name using case-insensitive substring matching. Results are paginated and
   * sorted. This endpoint is accessible without authentication.
   *
   * @param q    search term to match against POI names
   * @param page zero-based page index (default 0)
   * @param size number of items per page (default 10)
   * @param sort sorting criteria in format "property,direction" (default "id,desc")
   * @return Page of POIs matching the search criteria
   */
  @Operation(summary = "Search POIs", description = "Searches for POIs by name using case-insensitive substring matching. Results are paginated and sorted.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved matching POIs", 
          content = @Content(schema = @Schema(implementation = PoiItemDto.class)))
  })
  @GetMapping("/public/poi/search")
  public Page<PoiItemDto> searchPois(
      @RequestParam("q") String q,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "id,desc") String sort) {
    String[] parts = sort.split(",");
    Sort.Direction dir = parts.length > 1 && parts[1].equalsIgnoreCase("desc")
        ? Sort.Direction.DESC
        : Sort.Direction.ASC;
    Pageable pageable = PageRequest.of(page, size, Sort.by(dir, parts[0]));

    return poiService
        .searchPoisByName(q, pageable)
        .map(PoiItemDto::fromEntity);
  }
}
