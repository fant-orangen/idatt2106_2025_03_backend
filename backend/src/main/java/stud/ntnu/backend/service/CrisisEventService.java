package stud.ntnu.backend.service;

import org.springframework.stereotype.Service;
import stud.ntnu.backend.dto.map.CreateCrisisEventDto;
import stud.ntnu.backend.dto.map.UpdateCrisisEventDto;
import stud.ntnu.backend.model.map.CrisisEvent;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.repository.CrisisEventRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing crisis events. Handles creation, retrieval, updating, and deletion of crisis
 * events.
 */
@Service
public class CrisisEventService {

  private final CrisisEventRepository crisisEventRepository;

  /**
   * Constructor for dependency injection.
   *
   * @param crisisEventRepository repository for crisis event operations
   */
  public CrisisEventService(CrisisEventRepository crisisEventRepository) {
    this.crisisEventRepository = crisisEventRepository;
  }

  /**
   * Retrieves all crisis events.
   *
   * @return list of all crisis events
   */
  public List<CrisisEvent> getAllCrisisEvents() {
    return crisisEventRepository.findAll();
  }

  /**
   * Retrieves a crisis event by its ID.
   *
   * @param id the ID of the crisis event
   * @return an Optional containing the crisis event if found
   */
  public Optional<CrisisEvent> getCrisisEventById(Integer id) {
    return crisisEventRepository.findById(id);
  }

  /**
   * Saves a crisis event.
   *
   * @param crisisEvent the crisis event to save
   * @return the saved crisis event
   */
  public CrisisEvent saveCrisisEvent(CrisisEvent crisisEvent) {
    return crisisEventRepository.save(crisisEvent);
  }

  /**
   * Deletes a crisis event by its ID.
   *
   * @param id the ID of the crisis event to delete
   */
  public void deleteCrisisEvent(Integer id) {
    crisisEventRepository.deleteById(id);
  }

  /**
   * Creates a new crisis event.
   *
   * @param createCrisisEventDto the DTO containing crisis event information
   * @param currentUser the user creating the crisis event
   * @return the created crisis event
   */
  public CrisisEvent createCrisisEvent(CreateCrisisEventDto createCrisisEventDto, User currentUser) {
    // Create a new crisis event
    CrisisEvent crisisEvent = new CrisisEvent(
        createCrisisEventDto.getName(),
        createCrisisEventDto.getLatitude(),
        createCrisisEventDto.getLongitude(),
        createCrisisEventDto.getRadius(),
        LocalDateTime.now(),
        currentUser
    );

    // Set optional fields
    crisisEvent.setDescription(createCrisisEventDto.getDescription());
    crisisEvent.setSeverity(createCrisisEventDto.getSeverity());

    // Save and return the crisis event
    return saveCrisisEvent(crisisEvent);
  }

  /**
   * Updates an existing crisis event.
   *
   * @param id the ID of the crisis event to update
   * @param updateCrisisEventDto the DTO containing the update information
   * @return the updated crisis event
   * @throws IllegalStateException if the crisis event is not found
   */
  public CrisisEvent updateCrisisEvent(Integer id, UpdateCrisisEventDto updateCrisisEventDto) {
    // Get the crisis event by ID
    CrisisEvent crisisEvent = getCrisisEventById(id)
        .orElseThrow(() -> new IllegalStateException("Crisis event not found with ID: " + id));

    // Update fields if provided
    if (updateCrisisEventDto.getName() != null) {
      crisisEvent.setName(updateCrisisEventDto.getName());
    }

    if (updateCrisisEventDto.getDescription() != null) {
      crisisEvent.setDescription(updateCrisisEventDto.getDescription());
    }

    if (updateCrisisEventDto.getSeverity() != null) {
      crisisEvent.setSeverity(updateCrisisEventDto.getSeverity());
    }

    if (updateCrisisEventDto.getLatitude() != null) {
      crisisEvent.setEpicenterLatitude(updateCrisisEventDto.getLatitude());
    }

    if (updateCrisisEventDto.getLongitude() != null) {
      crisisEvent.setEpicenterLongitude(updateCrisisEventDto.getLongitude());
    }

    if (updateCrisisEventDto.getRadius() != null) {
      crisisEvent.setRadius(updateCrisisEventDto.getRadius());
    }

    // Save and return the updated crisis event
    return saveCrisisEvent(crisisEvent);
  }
}