package stud.ntnu.backend.controller.map;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.backend.dto.map.CreateMeetingPlaceDto;
import stud.ntnu.backend.dto.map.MeetingPlaceDto;
import stud.ntnu.backend.model.map.MeetingPlace;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.security.AdminChecker;
import stud.ntnu.backend.service.map.MeetingPlaceService;
import stud.ntnu.backend.service.user.UserService;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

/**
 * Manages meeting places. Supports CRUD operations for storing and retrieving
 * meeting places for crisis coordination.
 */
@RestController
@RequestMapping("/api")
public class MeetingPlaceController {

    private final MeetingPlaceService meetingPlaceService;
    private final UserService userService;

    public MeetingPlaceController(MeetingPlaceService meetingPlaceService, UserService userService) {
        this.meetingPlaceService = meetingPlaceService;
        this.userService = userService;
    }

    // TODO: this endpoint does not work yet because address -> coordinates is not implemented
    /**
     * Creates a new meeting place. Only accessible by admins.
     * TODO: test with postman
     * @param createDto the DTO containing meeting place information
     * @param principal the authenticated user
     * @return 200 OK on success
     */
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
     * Archives a meeting place. Only accessible by admins.
     * 
     * @param id the ID of the meeting place to archive
     * @param principal the authenticated user
     * @return the archived meeting place
     */
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
     * Activates a meeting place. Only accessible by admins.
     * 
     * @param id the ID of the meeting place to activate
     * @param principal the authenticated user
     * @return the activated meeting place
     */
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
     * Gets all active meeting places within the specified distance (defaults to 10km) of the location.
     * 
     * @param latitude the latitude of the location
     * @param longitude the longitude of the location
     * @param distance the distance in meters (optional, defaults to 10000)
     * @return list of nearby meeting places
     */
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

}
