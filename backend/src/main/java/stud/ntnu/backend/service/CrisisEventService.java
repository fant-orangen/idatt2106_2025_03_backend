package stud.ntnu.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.backend.dto.map.CreateCrisisEventDto;
import stud.ntnu.backend.dto.map.CrisisEventChangeDto;
import stud.ntnu.backend.dto.map.UpdateCrisisEventDto;
import stud.ntnu.backend.dto.map.CrisisEventPreviewDto;
import stud.ntnu.backend.dto.map.CrisisEventDetailsDto;
import stud.ntnu.backend.model.map.CrisisEvent;
import stud.ntnu.backend.model.map.CrisisEventChange;
import stud.ntnu.backend.model.user.Notification;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.repository.map.CrisisEventChangeRepository;
import stud.ntnu.backend.repository.map.CrisisEventRepository;
import stud.ntnu.backend.repository.map.ScenarioThemeRepository;
import stud.ntnu.backend.model.map.ScenarioTheme;
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
  private final CrisisEventChangeRepository crisisEventChangeRepository;
  private final NotificationService notificationService;
  private final UserService userService;
  private final ScenarioThemeRepository scenarioThemeRepository;
  private final Logger log = LoggerFactory.getLogger(CrisisEventService.class);

  /**
   * Constructor for dependency injection.
   *
   * @param crisisEventRepository repository for crisis event operationsd
   * @param notificationService   service for notification operations
   * @param userService           service for user operations
   * @param scenarioThemeRepository repository for scenario theme operations
   */
  public CrisisEventService(CrisisEventRepository crisisEventRepository,
      CrisisEventChangeRepository crisisEventChangeRepository,
      NotificationService notificationService,
      UserService userService,
      ScenarioThemeRepository scenarioThemeRepository) {
    this.crisisEventRepository = crisisEventRepository;
    this.crisisEventChangeRepository = crisisEventChangeRepository;
    this.notificationService = notificationService;
    this.userService = userService;
    this.scenarioThemeRepository = scenarioThemeRepository;
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
    CrisisEvent crisisEvent = crisisEventRepository.findById(id)
        .orElseThrow(() -> new IllegalStateException("Crisis event not found with ID: " + id));

    // Record the deactivation change
    CrisisEventChange change = new CrisisEventChange(
        crisisEvent,
        CrisisEventChange.ChangeType.level_change,
        "active: true",
        "active: false",
        crisisEvent.getCreatedByUser()
    );
    crisisEventChangeRepository.save(change);

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

    // Record the creation change
    CrisisEventChange change = new CrisisEventChange(
        savedCrisisEvent,
        CrisisEventChange.ChangeType.creation,
        null,
        "Created crisis event: " + savedCrisisEvent.getName(),
        currentUser
    );
    crisisEventChangeRepository.save(change);

    log.info("Sending notifications to users within the radius of the crisis event");
    // Send notifications to users within the radius
    notificationService.sendCrisisEventNotifications(savedCrisisEvent,
        "There is an ongoing crisis.");

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

    // Create a copy of the current state for comparison later
    CrisisEvent originalState = new CrisisEvent();
    originalState.setId(currentCrisisEvent.getId());
    originalState.setName(currentCrisisEvent.getName());
    originalState.setDescription(currentCrisisEvent.getDescription());
    originalState.setSeverity(currentCrisisEvent.getSeverity());
    originalState.setEpicenterLatitude(currentCrisisEvent.getEpicenterLatitude());
    originalState.setEpicenterLongitude(currentCrisisEvent.getEpicenterLongitude());
    originalState.setRadius(currentCrisisEvent.getRadius());
    originalState.setStartTime(currentCrisisEvent.getStartTime());
    originalState.setCreatedByUser(currentCrisisEvent.getCreatedByUser());
    originalState.setActive(currentCrisisEvent.getActive());

    // Option 1: Use the direct repository update method if all fields are provided
    if (updateCrisisEventDto.getName() != null &&
        updateCrisisEventDto.getDescription() != null &&
        updateCrisisEventDto.getSeverity() != null &&
        updateCrisisEventDto.getLatitude() != null &&
        updateCrisisEventDto.getLongitude() != null &&
        updateCrisisEventDto.getRadius() != null) {

      currentCrisisEvent.setName(updateCrisisEventDto.getName());
      currentCrisisEvent.setDescription(updateCrisisEventDto.getDescription());
      currentCrisisEvent.setSeverity(updateCrisisEventDto.getSeverity());
      currentCrisisEvent.setEpicenterLatitude(updateCrisisEventDto.getLatitude());
      currentCrisisEvent.setEpicenterLongitude(updateCrisisEventDto.getLongitude());
      currentCrisisEvent.setRadius(updateCrisisEventDto.getRadius());
      // Update scenario theme if provided
      if (updateCrisisEventDto.getScenarioThemeId() != null) {
        ScenarioTheme scenarioTheme = scenarioThemeRepository.findById(updateCrisisEventDto.getScenarioThemeId()).orElse(null);
        if (scenarioTheme == null) return null;
        currentCrisisEvent.setScenarioTheme(scenarioTheme);
      }
      // Then update in the database
      crisisEventRepository.updateCrisisEvent(
          id,
          updateCrisisEventDto.getName(),
          updateCrisisEventDto.getDescription(),
          updateCrisisEventDto.getSeverity(),
          updateCrisisEventDto.getLatitude(),
          updateCrisisEventDto.getLongitude(),
          updateCrisisEventDto.getRadius()
      );
      crisisEventRepository.flush();
      recordChanges(originalState, currentCrisisEvent, currentCrisisEvent.getCreatedByUser());
      notificationService.sendCrisisEventUpdateNotifications(currentCrisisEvent, originalState);
      return currentCrisisEvent;
    }

    // Option 2: Update only the provided fields
    boolean hasChanges = false;

    // Update the fields that are provided
    if (updateCrisisEventDto.getName() != null && !updateCrisisEventDto.getName()
        .equals(currentCrisisEvent.getName())) {
      currentCrisisEvent.setName(updateCrisisEventDto.getName());
      hasChanges = true;
    }
    if (updateCrisisEventDto.getDescription() != null &&
        (currentCrisisEvent.getDescription() == null ||
            !updateCrisisEventDto.getDescription().equals(currentCrisisEvent.getDescription()))) {
      currentCrisisEvent.setDescription(updateCrisisEventDto.getDescription());
      hasChanges = true;
    }
    if (updateCrisisEventDto.getSeverity() != null && !updateCrisisEventDto.getSeverity()
        .equals(currentCrisisEvent.getSeverity())) {
      currentCrisisEvent.setSeverity(updateCrisisEventDto.getSeverity());
      hasChanges = true;
    }
    if (updateCrisisEventDto.getLatitude() != null && !updateCrisisEventDto.getLatitude()
        .equals(currentCrisisEvent.getEpicenterLatitude())) {
      currentCrisisEvent.setEpicenterLatitude(updateCrisisEventDto.getLatitude());
      hasChanges = true;
    }
    if (updateCrisisEventDto.getLongitude() != null && !updateCrisisEventDto.getLongitude()
        .equals(currentCrisisEvent.getEpicenterLongitude())) {
      currentCrisisEvent.setEpicenterLongitude(updateCrisisEventDto.getLongitude());
      hasChanges = true;
    }
    if (updateCrisisEventDto.getRadius() != null &&
        (currentCrisisEvent.getRadius() == null ||
            !updateCrisisEventDto.getRadius().equals(currentCrisisEvent.getRadius()))) {
      currentCrisisEvent.setRadius(updateCrisisEventDto.getRadius());
      hasChanges = true;
    }
    // Update scenario theme if provided
    if (updateCrisisEventDto.getScenarioThemeId() != null) {
      ScenarioTheme scenarioTheme = scenarioThemeRepository.findById(updateCrisisEventDto.getScenarioThemeId()).orElse(null);
      if (scenarioTheme == null) return null;
      currentCrisisEvent.setScenarioTheme(scenarioTheme);
      hasChanges = true;
    }

    // Only save if there are changes
    if (hasChanges) {
      // Save directly using the repository
      CrisisEvent updatedCrisisEvent = crisisEventRepository.save(currentCrisisEvent);

      // Explicitly flush to ensure the changes are persisted
      crisisEventRepository.flush();

      // Record the changes
      recordChanges(originalState, updatedCrisisEvent, updatedCrisisEvent.getCreatedByUser());

      // Send notifications about the update
      notificationService.sendCrisisEventUpdateNotifications(updatedCrisisEvent, originalState);

      return updatedCrisisEvent;
    } else {
      // No changes, return the original entity
      return currentCrisisEvent;
    }
  }

  /**
   * Gets paginated crisis event changes for a specific crisis event.
   *
   * @param crisisEventId the ID of the crisis event
   * @param pageable      pagination information
   * @return a page of crisis event change DTOs
   * @throws IllegalStateException if the crisis event is not found
   */
  @Transactional(readOnly = true)
  public Page<CrisisEventChangeDto> getCrisisEventChanges(Integer crisisEventId,
      Pageable pageable) {
    // Check if the crisis event exists
    if (!crisisEventRepository.existsById(crisisEventId)) {
      throw new IllegalStateException("Crisis event not found with ID: " + crisisEventId);
    }

    // Get the crisis event changes
    Page<CrisisEventChange> changes = crisisEventChangeRepository.findByCrisisEventIdOrderByCreatedAtDesc(
        crisisEventId, pageable);

    // Convert to DTOs
    return changes.map(CrisisEventChangeDto::fromEntity);
  }

  /**
   * Records changes between two crisis event states.
   *
   * @param oldEvent the old crisis event state
   * @param newEvent the new crisis event state
   * @param user     the user who made the changes
   */
  private void recordChanges(CrisisEvent oldEvent, CrisisEvent newEvent, User user) {
    log.info("Recording changes for crisis event ID: {}", newEvent.getId());
    // Check for name change
    if (!oldEvent.getName().equals(newEvent.getName())) {
      CrisisEventChange change = new CrisisEventChange(
          newEvent,
          CrisisEventChange.ChangeType.description_update,
          "name: " + oldEvent.getName(),
          "name: " + newEvent.getName(),
          user
      );
      crisisEventChangeRepository.save(change);
      log.info("Recorded name change: {} -> {}", oldEvent.getName(), newEvent.getName());
    }

    // Check for description change
    if ((oldEvent.getDescription() == null && newEvent.getDescription() != null) ||
        (oldEvent.getDescription() != null && !oldEvent.getDescription()
            .equals(newEvent.getDescription()))) {
      CrisisEventChange change = new CrisisEventChange(
          newEvent,
          CrisisEventChange.ChangeType.description_update,
          "description: " + (oldEvent.getDescription() == null ? "null"
              : oldEvent.getDescription()),
          "description: " + (newEvent.getDescription() == null ? "null"
              : newEvent.getDescription()),
          user
      );
      crisisEventChangeRepository.save(change);
      log.info("Recorded description change");
    }

    // Check for severity change
    if (!oldEvent.getSeverity().equals(newEvent.getSeverity())) {
      CrisisEventChange change = new CrisisEventChange(
          newEvent,
          CrisisEventChange.ChangeType.level_change,
          "severity: " + oldEvent.getSeverity(),
          "severity: " + newEvent.getSeverity(),
          user
      );
      crisisEventChangeRepository.save(change);
      log.info("Recorded severity change: {} -> {}", oldEvent.getSeverity(),
          newEvent.getSeverity());
    }

    // Check for epicenter change
    if (!oldEvent.getEpicenterLatitude().equals(newEvent.getEpicenterLatitude()) ||
        !oldEvent.getEpicenterLongitude().equals(newEvent.getEpicenterLongitude())) {
      CrisisEventChange change = new CrisisEventChange(
          newEvent,
          CrisisEventChange.ChangeType.epicenter_moved,
          "location: [" + oldEvent.getEpicenterLatitude() + ", " + oldEvent.getEpicenterLongitude()
              + "]",
          "location: [" + newEvent.getEpicenterLatitude() + ", " + newEvent.getEpicenterLongitude()
              + "]",
          user
      );
      crisisEventChangeRepository.save(change);
      log.info("Recorded epicenter change");
    }

    // Check for radius change
    if ((oldEvent.getRadius() == null && newEvent.getRadius() != null) ||
        (oldEvent.getRadius() != null && newEvent.getRadius() != null && !oldEvent.getRadius()
            .equals(newEvent.getRadius()))) {
      CrisisEventChange change = new CrisisEventChange(
          newEvent,
          CrisisEventChange.ChangeType.epicenter_moved,
          "radius: " + (oldEvent.getRadius() == null ? "null" : oldEvent.getRadius()),
          "radius: " + (newEvent.getRadius() == null ? "null" : newEvent.getRadius()),
          user
      );
      crisisEventChangeRepository.save(change);
      log.info("Recorded radius change");
    }
  }

  /**
   * Retrieves a paginated list of crisis events affecting the given user. A crisis event affects a
   * user if the user's home or household location is within the event's radius.
   *
   * @param user     the user to check
   * @param pageable pagination information
   * @return a page of crisis events affecting the user
   */
  @Transactional(readOnly = true)
  public Page<CrisisEvent> getCrisisEventsAffectingUser(User user, Pageable pageable) {
    // Get all active crisis events (could be optimized with a custom query if needed)
    List<CrisisEvent> allActiveEvents = crisisEventRepository.findByActiveTrue();
    // Filter events that affect the user
    List<CrisisEvent> affectingEvents = allActiveEvents.stream()
        .filter(event -> event.getRadius() != null &&
            LocationUtil.isCrisisEventNearUser(user, event, event.getRadius().doubleValue()))
        .toList();
    // Manual pagination
    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), affectingEvents.size());
    List<CrisisEvent> pagedList = (start <= end) ? affectingEvents.subList(start, end) : List.of();
    return new PageImpl<>(pagedList, pageable, affectingEvents.size());
  }

  /**
   * Retrieves a preview (id, name, severity, startTime) of all active crisis events with
   * pagination.
   *
   * @param pageable pagination information
   * @return page of crisis event previews
   */
  @Transactional(readOnly = true)
  public Page<CrisisEventPreviewDto> getAllCrisisEventPreviews(Pageable pageable) {
    List<CrisisEvent> activeEvents = crisisEventRepository.findByActiveTrue();
    List<CrisisEventPreviewDto> previews = activeEvents.stream()
        .map(CrisisEventPreviewDto::fromEntity)
        .toList();
    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), previews.size());
    List<CrisisEventPreviewDto> pagedList =
        (start <= end) ? previews.subList(start, end) : List.of();
    return new PageImpl<>(pagedList, pageable, previews.size());
  }

  /**
   * Retrieves a paginated list of crisis event previews affecting the given user, sorted by
   * severity (red > yellow > green).
   *
   * @param user     the user to check
   * @param pageable pagination information
   * @return a page of crisis event previews affecting the user
   */
  @Transactional(readOnly = true)
  public Page<CrisisEventPreviewDto> getCrisisEventPreviewsAffectingUserSortedBySeverity(User user,
      Pageable pageable) {
    log.info("Getting crisis event previews affecting user: {}", user.getEmail());
    Page<CrisisEvent> eventsPage = getCrisisEventsAffectingUser(user, pageable);
    log.info("Affecting events count: {}", eventsPage.getTotalElements());
    // Map to preview DTOs
    List<CrisisEventPreviewDto> previews = eventsPage.getContent().stream()
        .map(CrisisEventPreviewDto::fromEntity)
        .toList();
    // Sort by severity: red > yellow > green
    previews = previews.stream()
        .sorted((a, b) -> Integer.compare(severityOrder(b.getSeverity()), severityOrder(a.getSeverity())))
        .toList();
    return new PageImpl<>(previews, pageable, eventsPage.getTotalElements());
  }

  private int severityOrder(CrisisEvent.Severity severity) {
    return switch (severity) {
      case red -> 3;
      case yellow -> 2;
      case green -> 1;
    };
  }

  /**
   * Retrieves a crisis event details DTO by its ID.
   *
   * @param id the ID of the crisis event
   * @return an Optional containing the crisis event details DTO if found
   */
  @Transactional(readOnly = true)
  public Optional<CrisisEventDetailsDto> getCrisisEventDetailsById(Integer id) {
    return crisisEventRepository.findById(id).map(CrisisEventDetailsDto::fromEntity);
  }
}