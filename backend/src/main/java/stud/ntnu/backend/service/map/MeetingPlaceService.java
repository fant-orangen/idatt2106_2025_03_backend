package stud.ntnu.backend.service.map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.backend.dto.map.CreateMeetingPlaceDto;
import stud.ntnu.backend.model.map.MeetingPlace;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.repository.map.MeetingPlaceRepository;
import stud.ntnu.backend.util.LocationUtil;
import stud.ntnu.backend.dto.map.CoordinatesItemDto;
import stud.ntnu.backend.dto.map.MeetingPlacePreviewDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing meeting places. Handles creation, retrieval, updating, and deletion of
 * meeting places.
 */
@Service
public class MeetingPlaceService {

  private final MeetingPlaceRepository meetingPlaceRepository;

  /**
   * Constructor for dependency injection.
   *
   * @param meetingPlaceRepository repository for meeting place operations
   */
  public MeetingPlaceService(MeetingPlaceRepository meetingPlaceRepository) {
    this.meetingPlaceRepository = meetingPlaceRepository;
  }

  /**
   * Retrieves all meeting places.
   *
   * @return list of all meeting places
   */
  public List<MeetingPlace> getAllMeetingPlaces() {
    return meetingPlaceRepository.findAll();
  }

  /**
   * Retrieves a meeting place by its ID.
   *
   * @param id the ID of the meeting place
   * @return an Optional containing the meeting place if found
   */
  public Optional<MeetingPlace> getMeetingPlaceById(Integer id) {
    return meetingPlaceRepository.findById(id);
  }

  /**
   * Saves a meeting place.
   *
   * @param meetingPlace the meeting place to save
   * @return the saved meeting place
   */
  public MeetingPlace saveMeetingPlace(MeetingPlace meetingPlace) {
    return meetingPlaceRepository.save(meetingPlace);
  }

  /**
   * Deletes a meeting place by its ID.
   *
   * @param id the ID of the meeting place to delete
   */
  public void deleteMeetingPlace(Integer id) {
    meetingPlaceRepository.deleteById(id);
  }

  /**
   * Creates a new meeting place with the provided details.
   * If an address is provided but coordinates are not, the address will be geocoded to obtain coordinates.
   * This operation is transactional to ensure data consistency.
   *
   * @param createDto the DTO containing the meeting place creation details
   * @param currentUser the user creating the meeting place
   * @return the newly created meeting place
   */
  @Transactional
  public MeetingPlace createMeetingPlace(CreateMeetingPlaceDto createDto, User currentUser) {
    // If address is provided but not coordinates, convert address to coordinates
    if (createDto.getAddress() != null && (createDto.getLatitude() == null || createDto.getLongitude() == null)) {
      CoordinatesItemDto coordinates = LocationUtil.getCoordinatesByAddress(createDto.getAddress());
      // Create meeting place with converted coordinates
      MeetingPlace meetingPlace = new MeetingPlace(
        createDto.getName(),
        coordinates.getLatitude(),
        coordinates.getLongitude(),
        currentUser
      );
      meetingPlace.setAddress(createDto.getAddress());
      return meetingPlaceRepository.save(meetingPlace);
    }

    // If coordinates are provided directly
    MeetingPlace meetingPlace = new MeetingPlace(
      createDto.getName(),
      createDto.getLatitude(),
      createDto.getLongitude(),
      currentUser
    );
    meetingPlace.setAddress(createDto.getAddress());

    return meetingPlaceRepository.save(meetingPlace);
  }

  /**
   * Archives a meeting place by setting its status to "archived".
   * This operation is transactional to ensure data consistency.
   *
   * @param id the ID of the meeting place to archive
   * @return the archived meeting place
   * @throws IllegalStateException if the meeting place is not found
   */
  @Transactional
  public MeetingPlace archiveMeetingPlace(Integer id) {
    MeetingPlace meetingPlace = meetingPlaceRepository.findById(id)
      .orElseThrow(() -> new IllegalStateException("Meeting place not found"));
    meetingPlace.setStatus("archived");
    return meetingPlaceRepository.save(meetingPlace);
  }

  /**
   * Activates a meeting place by setting its status to "active".
   * This operation is transactional to ensure data consistency.
   *
   * @param id the ID of the meeting place to activate
   * @return the activated meeting place
   * @throws IllegalStateException if the meeting place is not found
   */
  @Transactional
  public MeetingPlace activateMeetingPlace(Integer id) {
    MeetingPlace meetingPlace = meetingPlaceRepository.findById(id)
      .orElseThrow(() -> new IllegalStateException("Meeting place not found"));
    meetingPlace.setStatus("active");
    return meetingPlaceRepository.save(meetingPlace);
  }
  /**
   * Retrieves a list of active meeting places within a specified distance from given coordinates.
   * The distance calculation is performed using the Haversine formula through LocationUtil.
   * Only meeting places with "active" status are considered.
   *
   * @param latitude the latitude coordinate of the center point
   * @param longitude the longitude coordinate of the center point
   * @param maxDistanceKm the maximum distance in kilometers from the center point
   * @return a list of meeting places within the specified distance
   */
  public List<MeetingPlace> getNearbyMeetingPlaces(BigDecimal latitude, BigDecimal longitude, double maxDistanceKm) {
    List<MeetingPlace> allMeetingPlaces = meetingPlaceRepository.findByStatus("active");
    
    return allMeetingPlaces.stream()
      .filter(place -> LocationUtil.calculateDistance(
        latitude.doubleValue(),
        longitude.doubleValue(),
        place.getLatitude().doubleValue(),
        place.getLongitude().doubleValue()
      ) <= maxDistanceKm * 1000) // Convert km to meters
      .toList();
  }

  /**
   * Retrieves a paginated list of all meeting places.
   *
   * @param page the page number (0-based)
   * @param size the size of each page
   * @return paginated list of meeting places
   */
  public Page<MeetingPlace> getAllMeetingPlacesPaginated(int page, int size) {
    PageRequest pageRequest = PageRequest.of(page, size);
    return meetingPlaceRepository.findAll(pageRequest);
  }

  /**
   * Retrieves a paginated list of meeting place previews (only id and name).
   *
   * @param page the page number (0-based)
   * @param size the size of each page
   * @return paginated list of meeting place previews
   */
  public Page<MeetingPlacePreviewDto> getAllMeetingPlacePreviewsPaginated(int page, int size) {
    PageRequest pageRequest = PageRequest.of(page, size);
    return meetingPlaceRepository.findAll(pageRequest).map(MeetingPlacePreviewDto::fromEntity);
  }
}