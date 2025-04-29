package stud.ntnu.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.backend.dto.map.CreateCrisisEventDto;
import stud.ntnu.backend.dto.map.UpdateCrisisEventDto;
import stud.ntnu.backend.model.map.CrisisEvent;
import stud.ntnu.backend.model.user.Notification;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.repository.map.CrisisEventRepository;
import stud.ntnu.backend.util.LocationUtil;

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
  private final NotificationService notificationService;
  private final UserService userService;
  private final Logger log = LoggerFactory.getLogger(CrisisEventService.class);

  /**
   * Constructor for dependency injection.
   *
   * @param crisisEventRepository repository for crisis event operations
   * @param notificationService   service for notification operations
   * @param userService          service for user operations
   */
  public CrisisEventService(CrisisEventRepository crisisEventRepository,
      NotificationService notificationService,
      UserService userService) {
    this.crisisEventRepository = crisisEventRepository;
    this.notificationService = notificationService;
    this.userService = userService;
  }

  /**
   * Retrieves all crisis events with pagination.
   *
   * @param pageable pagination information
   * @return page of crisis events
   */
  @Transactional(readOnly = true)
  public Page<CrisisEvent> getAllCrisisEvents(Pageable pageable) {
    return crisisEventRepository.findAll(pageable);
  }


  /**
   * Retrieves a crisis event by its ID.
   *
   * @param id the ID of the crisis event
   * @return an Optional containing the crisis event if found
   */
  @Transactional(readOnly = true)
  public Optional<CrisisEvent> getCrisisEventById(Integer id) {
    return crisisEventRepository.findById(id);
  }

  /**
   * Saves a crisis event.
   *
   * @param crisisEvent the crisis event to save
   * @return the saved crisis event
   */
  @Transactional
  public CrisisEvent saveCrisisEvent(CrisisEvent crisisEvent) {
    return crisisEventRepository.save(crisisEvent);
  }

  /**
   * Deactivates a crisis event (marks it as inactive) instead of deleting it.
   *
   * @param id the ID of the crisis event to deactivate
   */
  @Transactional
  public void deactivateCrisisEvent(Integer id) {
    crisisEventRepository.deactivateCrisisEvent(id);
  }

  /**
   * Creates a new crisis event.
   *
   * @param createCrisisEventDto the DTO containing crisis event information
   * @param currentUser          the user creating the crisis event
   * @return the created crisis event
   */
  @Transactional
  public CrisisEvent createCrisisEvent(CreateCrisisEventDto createCrisisEventDto,
      User currentUser) {
    // Create a new crisis event with start time from DTO
    CrisisEvent crisisEvent = new CrisisEvent(
        createCrisisEventDto.getName(),
        createCrisisEventDto.getLatitude(),
        createCrisisEventDto.getLongitude(),
        createCrisisEventDto.getRadius(),
        createCrisisEventDto.getStartTime(),
        currentUser
    );

    // Set optional fields
    crisisEvent.setDescription(createCrisisEventDto.getDescription());
    crisisEvent.setSeverity(createCrisisEventDto.getSeverity());

    // Save the crisis event
    CrisisEvent savedCrisisEvent = crisisEventRepository.save(crisisEvent);

    log.info("Sending notifications to users within the radius of the crisis event");
    // Send notifications to users within the radius
    notificationService.sendCrisisEventNotifications(savedCrisisEvent, "There is an ongoing crisis.");

    return savedCrisisEvent;
  }

  /**
   * Updates an existing crisis event. Note: The start time of a crisis event cannot be updated
   * after creation.
   *
   * @param id                   the ID of the crisis event to update
   * @param updateCrisisEventDto the DTO containing the update information
   * @return the updated crisis event
   * @throws IllegalStateException if the crisis event is not found
   */
  @Transactional
  public CrisisEvent updateCrisisEvent(Integer id, UpdateCrisisEventDto updateCrisisEventDto) {
    // First check if the crisis event exists
    if (!crisisEventRepository.existsById(id)) {
      throw new IllegalStateException("Crisis event not found with ID: " + id);
    }

    // Get the current crisis event
    CrisisEvent currentCrisisEvent = crisisEventRepository.findById(id)
        .orElseThrow(() -> new IllegalStateException("Crisis event not found with ID: " + id));

    // Option 1: Use the direct repository update method if all fields are provided
    if (updateCrisisEventDto.getName() != null &&
        updateCrisisEventDto.getDescription() != null &&
        updateCrisisEventDto.getSeverity() != null &&
        updateCrisisEventDto.getLatitude() != null &&
        updateCrisisEventDto.getLongitude() != null &&
        updateCrisisEventDto.getRadius() != null) {

      crisisEventRepository.updateCrisisEvent(
          id,
          updateCrisisEventDto.getName(),
          updateCrisisEventDto.getDescription(),
          updateCrisisEventDto.getSeverity(),
          updateCrisisEventDto.getLatitude(),
          updateCrisisEventDto.getLongitude(),
          updateCrisisEventDto.getRadius()
      );

      // Return the updated entity
      CrisisEvent updatedCrisisEvent = crisisEventRepository.findById(id).orElseThrow();
      
      // Send notifications about the update
      notificationService.sendCrisisEventUpdateNotifications(updatedCrisisEvent, currentCrisisEvent);
      
      return updatedCrisisEvent;
    }

    // Option 2: For partial updates, use the traditional approach
    CrisisEvent crisisEvent = crisisEventRepository.findById(id)
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

    // Save directly using the repository
    CrisisEvent updatedCrisisEvent = crisisEventRepository.save(crisisEvent);
    
    // Send notifications about the update
    notificationService.sendCrisisEventUpdateNotifications(updatedCrisisEvent, currentCrisisEvent);
    
    return updatedCrisisEvent;
  }
}