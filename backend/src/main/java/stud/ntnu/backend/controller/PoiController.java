package stud.ntnu.backend.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.backend.dto.poi.CreatePoiDto;
import stud.ntnu.backend.dto.poi.PoiItemDto;
import stud.ntnu.backend.model.map.PointOfInterest;
import stud.ntnu.backend.model.map.PoiType;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.security.AdminChecker;
import stud.ntnu.backend.service.PoiService;
import stud.ntnu.backend.service.UserService;
import stud.ntnu.backend.util.LocationUtil;

import java.security.Principal;
import java.util.List;

/**
 * Provides access to public Points of Interest (POIs).
 * Includes tilfluktsrom, hjertestartere, matstasjoner,
 * and similar resources, with map-based filtering support.
 * Based on Visjonsdokument 2025 for Krisefikser.no.
 */


@RestController
@RequestMapping("/api/poi")
public class PoiController {
    private final PoiService poiService;
    private final UserService userService;

    public PoiController(PoiService poiService, UserService userService) {
        this.poiService = poiService;
        this.userService = userService;
    }

    /**
     * Retrieves all public points of interest.
     *
     * @return a list of all public points of interest
     */
    @GetMapping("/public")
    public List<PoiItemDto> getPublicPointsOfInterest() {
        return poiService.getAllPointsOfInterest()
                .stream()
                .map(PoiItemDto::fromEntity)
                .toList();
    }
    /**
     * Retrieves all points of interest of a specific type.
     *
     * @return a list of all points of interest of a specific type
     */
    @GetMapping("/type/{id}")
    public List<PoiItemDto> getPointsOfInterestByTypeId(@PathVariable int id) {
        return poiService.getPointsOfInterestByTypeId(id)
                .stream()
                .map(PoiItemDto::fromEntity)
                .toList();
    }
    /**
     * Retrieves poi by id
     *
     * @param id the id of the poi
     * @return the poi with the given id as a PoiItemDto
     */
    @GetMapping("/{id}")
    public PoiItemDto getPointOfInterestById(@PathVariable int id) {
        return poiService.getPointOfInterestById(id)
                .map(PoiItemDto::fromEntity)
                .orElse(null);
    }

    /**
     * Retrieves all points of interest of a specific type within a given distance from a specified location.
     *
     * @param id        the ID of the point of interest type (optional)
     * @param latitude  the latitude of the location
     * @param longitude the longitude of the location
     * @param distance  the distance in meters
     * @return a list of points of interest within the specified distance
     */
    //To test with postman http://localhost:8080/api/poi/type/nearby?latitude=63.4308&longitude=10.3943&distance=1000
    @GetMapping("/type/nearby")
    public List<PoiItemDto> getPointsOfInterestByTypeIdAndDistance(
            @RequestParam(required = false) Integer id,
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam double distance) {
        return (id == null ? poiService.getAllPointsOfInterest() : poiService.getPointsOfInterestByTypeId(id))
                .stream()
                .filter(poi -> LocationUtil.calculateDistance(latitude, longitude,
                        poi.getLatitude().doubleValue(), poi.getLongitude().doubleValue()) <= distance)
                .map(PoiItemDto::fromEntity)
                .toList();
    }
    /**
     * Retrieves the nearest point of interest of a specific type from a given location.
     *
     * @param id        the ID of the point of interest type
     * @param latitude  the latitude of the location
     * @param longitude the longitude of the location
     * @return the nearest point of interest of the specified type
     */
    @GetMapping("/type/nearest/{id}")
    public PoiItemDto getNearestPointOfInterestByType(
            @PathVariable int id,
            @RequestParam double latitude,
            @RequestParam double longitude) {
        PointOfInterest nearestPoi = PoiService.findNearestPoi(latitude, longitude, poiService.getPointsOfInterestByTypeId(id));
        return nearestPoi != null ? PoiItemDto.fromEntity(nearestPoi) : null;
    }
    //javadoc for createPointOfInterest
    /**
     * Creates a new point of interest. Only admin and superadmin can create points of interest.
     *
     * @param createPoiDto the DTO containing point of interest information
     * @param principal    the authenticated user
     * @return the created point of interest
     */
    @PostMapping
    public ResponseEntity<?> createPointOfInterest(
            @Valid @RequestBody CreatePoiDto createPoiDto,
            Principal principal) {
        try {
            // Check if the current user is an admin using AdminChecker with Principal
            if (!AdminChecker.isCurrentUserAdmin(principal, userService)) {
                return ResponseEntity.status(403).body("Only administrators can create points of interest");
            }

            // Get the current authenticated user
            String email = principal.getName();
            User currentUser = userService.getUserByEmail(email)
                    .orElseThrow(() -> new IllegalStateException("User not found"));

            // Delegate the creation logic to the service
            PointOfInterest savedPoi = poiService.createPointOfInterest(createPoiDto, currentUser);

            // Return the created POI
            return ResponseEntity.ok(PoiItemDto.fromEntity(savedPoi));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
