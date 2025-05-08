package stud.ntnu.backend.service.crisis;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import stud.ntnu.backend.dto.map.CreateCrisisEventDto;
import stud.ntnu.backend.dto.map.CrisisEventChangeDto;
import stud.ntnu.backend.dto.map.UpdateCrisisEventDto;
import stud.ntnu.backend.dto.map.CrisisEventPreviewDto;
import stud.ntnu.backend.dto.map.CrisisEventDetailsDto;
import stud.ntnu.backend.model.map.CrisisEvent;
import stud.ntnu.backend.model.map.CrisisEventChange;
import stud.ntnu.backend.model.map.ScenarioTheme;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.model.user.Notification;
import stud.ntnu.backend.repository.map.CrisisEventChangeRepository;
import stud.ntnu.backend.repository.map.CrisisEventRepository;
import stud.ntnu.backend.repository.map.ScenarioThemeRepository;
import stud.ntnu.backend.service.user.NotificationService;
import stud.ntnu.backend.service.user.UserService;
import stud.ntnu.backend.util.LocationUtil;
import stud.ntnu.backend.util.SearchUtil;

/**
 * Service responsible for managing crisis events in the system.
 * Provides functionality for creating, updating, deactivating, and querying crisis events,
 * as well as managing notifications for affected users.
 * Supports various filtering and sorting options for crisis event retrieval.
 */
@Service
public class CrisisEventService {

    private final CrisisEventRepository crisisEventRepository;
    private final CrisisEventChangeRepository crisisEventChangeRepository;
    private final NotificationService notificationService;
    private final UserService userService;
    private final ScenarioThemeRepository scenarioThemeRepository;
    private final MessageSource messageSource;

    @Autowired
    private SearchUtil searchUtil;

    /**
     * Constructs a new CrisisEventService with the required dependencies.
     *
     * @param crisisEventRepository repository for crisis event operations
     * @param crisisEventChangeRepository repository for tracking crisis event changes
     * @param notificationService service for managing notifications
     * @param userService service for user operations
     * @param scenarioThemeRepository repository for scenario theme operations
     * @param messageSource source for internationalized messages
     */
    public CrisisEventService(
            CrisisEventRepository crisisEventRepository,
            CrisisEventChangeRepository crisisEventChangeRepository,
            NotificationService notificationService,
            UserService userService,
            ScenarioThemeRepository scenarioThemeRepository,
            MessageSource messageSource) {
        this.crisisEventRepository = crisisEventRepository;
        this.crisisEventChangeRepository = crisisEventChangeRepository;
        this.notificationService = notificationService;
        this.userService = userService;
        this.scenarioThemeRepository = scenarioThemeRepository;
        this.messageSource = messageSource;
    }

    /**
     * Retrieves all crisis events with pagination support.
     *
     * @param pageable pagination information including page number, size, and sorting
     * @return Page of crisis events
     */
    @Transactional(readOnly = true)
    public Page<CrisisEvent> getAllCrisisEvents(Pageable pageable) {
        return crisisEventRepository.findAll(pageable);
    }

    /**
     * Retrieves a specific crisis event by its ID.
     *
     * @param id the ID of the crisis event to retrieve
     * @return Optional containing the crisis event if found, empty otherwise
     */
    @Transactional(readOnly = true)
    public Optional<CrisisEvent> getCrisisEventById(Integer id) {
        return crisisEventRepository.findById(id);
    }

    /**
     * Saves a crisis event to the database.
     *
     * @param crisisEvent the crisis event to save
     * @return the saved crisis event with updated information (e.g., generated ID)
     */
    @Transactional
    public CrisisEvent saveCrisisEvent(CrisisEvent crisisEvent) {
        return crisisEventRepository.save(crisisEvent);
    }

    /**
     * Deactivates a crisis event and notifies affected users.
     * The event is not deleted but marked as inactive, preserving historical data.
     *
     * @param id the ID of the crisis event to deactivate
     * @throws IllegalStateException if the crisis event is not found
     */
    @Transactional
    public void deactivateCrisisEvent(Integer id) {
        CrisisEvent crisisEvent = crisisEventRepository.findById(id)
            .orElseThrow(() -> new IllegalStateException("Crisis event not found with ID: " + id));

        CrisisEventChange change = new CrisisEventChange(
            crisisEvent,
            CrisisEventChange.ChangeType.level_change,
            "active: true",
            "active: false",
            crisisEvent.getCreatedByUser()
        );
        crisisEventChangeRepository.save(change);

        List<User> affectedUsers = userService.getAllUsers().stream()
            .filter(user -> user.getHousehold() != null 
                && user.getHousehold().getLatitude() != null 
                && user.getHousehold().getLongitude() != null
                && LocationUtil.calculateDistance(
                    crisisEvent.getEpicenterLatitude().doubleValue(),
                    crisisEvent.getEpicenterLongitude().doubleValue(),
                    user.getHousehold().getLatitude().doubleValue(),
                    user.getHousehold().getLongitude().doubleValue()
                ) <= crisisEvent.getRadius().doubleValue() * 1000
            )
            .toList();

        crisisEventRepository.deactivateCrisisEvent(id);

        String notificationMessage = messageSource.getMessage(
            "notification.crisis.deactivated",
            new Object[]{crisisEvent.getName()},
            LocaleContextHolder.getLocale()
        );

        for (User user : affectedUsers) {
            Notification notification = notificationService.createNotification(
                user,
                Notification.PreferenceType.crisis_alert,
                Notification.TargetType.event,
                crisisEvent.getId(),
                notificationMessage
            );
            notificationService.sendNotification(notification);
        }
    }

    /**
     * Creates a new crisis event and notifies affected users.
     *
     * @param createCrisisEventDto DTO containing the crisis event information
     * @param currentUser the user creating the crisis event
     * @return the created crisis event
     */
    @Transactional
    public CrisisEvent createCrisisEvent(CreateCrisisEventDto createCrisisEventDto, User currentUser) {
        CrisisEvent crisisEvent = new CrisisEvent(
            createCrisisEventDto.getName(),
            createCrisisEventDto.getLatitude(),
            createCrisisEventDto.getLongitude(),
            createCrisisEventDto.getRadius(),
            createCrisisEventDto.getStartTime(),
            currentUser
        );

        crisisEvent.setDescription(createCrisisEventDto.getDescription());
        crisisEvent.setSeverity(createCrisisEventDto.getSeverity());

        if (createCrisisEventDto.getScenarioThemeId() != null) {
            ScenarioTheme scenarioTheme = scenarioThemeRepository.findById(
                createCrisisEventDto.getScenarioThemeId()).orElse(null);
            if (scenarioTheme != null) {
                crisisEvent.setScenarioTheme(scenarioTheme);
            }
        }

        CrisisEvent savedCrisisEvent = crisisEventRepository.save(crisisEvent);

        CrisisEventChange change = new CrisisEventChange(
            savedCrisisEvent,
            CrisisEventChange.ChangeType.creation,
            null,
            "Created crisis event: " + savedCrisisEvent.getName(),
            currentUser
        );
        crisisEventChangeRepository.save(change);

        notificationService.sendCrisisEventNotifications(savedCrisisEvent);

        return savedCrisisEvent;
    }

    /**
     * Updates an existing crisis event and notifies affected users of changes.
     * The start time of a crisis event cannot be updated after creation.
     *
     * @param id the ID of the crisis event to update
     * @param updateCrisisEventDto DTO containing the updated information
     * @return the updated crisis event
     * @throws IllegalStateException if the crisis event is not found
     */
    @Transactional
    public CrisisEvent updateCrisisEvent(Integer id, UpdateCrisisEventDto updateCrisisEventDto) {
        if (!crisisEventRepository.existsById(id)) {
            throw new IllegalStateException("Crisis event not found with ID: " + id);
        }

        CrisisEvent currentCrisisEvent = crisisEventRepository.findById(id)
            .orElseThrow(() -> new IllegalStateException("Crisis event not found with ID: " + id));

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

            if (updateCrisisEventDto.getScenarioThemeId() != null) {
                ScenarioTheme scenarioTheme = scenarioThemeRepository.findById(
                    updateCrisisEventDto.getScenarioThemeId()).orElse(null);
                if (scenarioTheme == null) {
                    return null;
                }
                currentCrisisEvent.setScenarioTheme(scenarioTheme);
            }

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

        boolean hasChanges = false;

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

        if (updateCrisisEventDto.getScenarioThemeId() != null) {
            ScenarioTheme scenarioTheme = scenarioThemeRepository.findById(
                updateCrisisEventDto.getScenarioThemeId()).orElse(null);
            if (scenarioTheme == null) {
                return null;
            }
            currentCrisisEvent.setScenarioTheme(scenarioTheme);
            hasChanges = true;
        }

        if (hasChanges) {
            CrisisEvent updatedCrisisEvent = crisisEventRepository.save(currentCrisisEvent);
            crisisEventRepository.flush();
            recordChanges(originalState, updatedCrisisEvent, updatedCrisisEvent.getCreatedByUser());
            notificationService.sendCrisisEventUpdateNotifications(updatedCrisisEvent, originalState);
            return updatedCrisisEvent;
        } else {
            return currentCrisisEvent;
        }
    }

    /**
     * Retrieves the change history for a specific crisis event with pagination.
     *
     * @param crisisEventId the ID of the crisis event
     * @param pageable pagination information
     * @return Page of crisis event changes
     * @throws IllegalStateException if the crisis event is not found
     */
    @Transactional(readOnly = true)
    public Page<CrisisEventChangeDto> getCrisisEventChanges(Integer crisisEventId, Pageable pageable) {
        if (!crisisEventRepository.existsById(crisisEventId)) {
            throw new IllegalStateException("Crisis event not found with ID: " + crisisEventId);
        }

        Page<CrisisEventChange> changes = crisisEventChangeRepository
            .findByCrisisEventIdOrderByCreatedAtDesc(crisisEventId, pageable);

        return changes.map(CrisisEventChangeDto::fromEntity);
    }

    /**
     * Records changes made to a crisis event for audit purposes.
     *
     * @param oldEvent the previous state of the crisis event
     * @param newEvent the new state of the crisis event
     * @param user the user who made the changes
     */
    private void recordChanges(CrisisEvent oldEvent, CrisisEvent newEvent, User user) {
        if (!oldEvent.getName().equals(newEvent.getName())) {
            CrisisEventChange change = new CrisisEventChange(
                newEvent,
                CrisisEventChange.ChangeType.description_update,
                "name: " + oldEvent.getName(),
                "name: " + newEvent.getName(),
                user
            );
            crisisEventChangeRepository.save(change);
        }

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
        }

        if (!oldEvent.getSeverity().equals(newEvent.getSeverity())) {
            CrisisEventChange change = new CrisisEventChange(
                newEvent,
                CrisisEventChange.ChangeType.level_change,
                "severity: " + oldEvent.getSeverity(),
                "severity: " + newEvent.getSeverity(),
                user
            );
            crisisEventChangeRepository.save(change);
        }

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
        }

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
        }
    }

    /**
     * Retrieves crisis events affecting a specific user.
     * A crisis event affects a user if their household location is within the event's radius.
     *
     * @param user the user to check
     * @param pageable pagination information
     * @return Page of crisis events affecting the user, sorted by severity (red > yellow > green)
     */
    @Transactional(readOnly = true)
    public Page<CrisisEvent> getCrisisEventsAffectingUser(User user, Pageable pageable) {
        List<CrisisEvent> allActiveEvents = crisisEventRepository.findByActiveTrue();
        List<CrisisEvent> affectingEvents = allActiveEvents.stream()
            .filter(event -> event.getRadius() != null &&
                LocationUtil.isCrisisEventNearUser(user, event, event.getRadius().doubleValue()))
            .sorted((a, b) -> Integer.compare(severityOrder(b.getSeverity()),
                severityOrder(a.getSeverity())))
            .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), affectingEvents.size());
        List<CrisisEvent> pagedList = (start <= end) ? affectingEvents.subList(start, end) : List.of();
        return new PageImpl<>(pagedList, pageable, affectingEvents.size());
    }

    /**
     * Retrieves previews of all active crisis events.
     *
     * @param pageable pagination information
     * @return Page of crisis event previews, sorted by severity (red > yellow > green)
     */
    @Transactional(readOnly = true)
    public Page<CrisisEventPreviewDto> getAllCrisisEventPreviews(Pageable pageable) {
        List<CrisisEvent> activeEvents = crisisEventRepository.findByActiveTrue();
        List<CrisisEventPreviewDto> previews = activeEvents.stream()
            .map(CrisisEventPreviewDto::fromEntity)
            .sorted((a, b) -> Integer.compare(severityOrder(b.getSeverity()),
                severityOrder(a.getSeverity())))
            .toList();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), previews.size());
        List<CrisisEventPreviewDto> pagedList =
            (start <= end) ? previews.subList(start, end) : List.of();
        return new PageImpl<>(pagedList, pageable, previews.size());
    }

    /**
     * Converts a severity level to a numeric value for sorting purposes.
     *
     * @param severity the severity level to convert
     * @return numeric value (red=3, yellow=2, green=1)
     */
    private int severityOrder(CrisisEvent.Severity severity) {
        return switch (severity) {
            case red -> 3;
            case yellow -> 2;
            case green -> 1;
        };
    }

    /**
     * Retrieves previews of crisis events affecting a specific user.
     *
     * @param user the user to check
     * @param pageable pagination information
     * @return Page of crisis event previews affecting the user, sorted by severity
     */
    @Transactional(readOnly = true)
    public Page<CrisisEventPreviewDto> getCrisisEventPreviewsAffectingUserSortedBySeverity(
            User user, Pageable pageable) {
        Page<CrisisEvent> eventsPage = getCrisisEventsAffectingUser(user, pageable);
        List<CrisisEventPreviewDto> previews = eventsPage.getContent().stream()
            .map(CrisisEventPreviewDto::fromEntity)
            .sorted((a, b) -> Integer.compare(severityOrder(b.getSeverity()),
                severityOrder(a.getSeverity())))
            .toList();
        return new PageImpl<>(previews, pageable, eventsPage.getTotalElements());
    }

    /**
     * Retrieves detailed information about a specific crisis event.
     *
     * @param id the ID of the crisis event
     * @return Optional containing the crisis event details if found
     */
    @Transactional(readOnly = true)
    public Optional<CrisisEventDetailsDto> getCrisisEventDetailsById(Integer id) {
        return crisisEventRepository.findById(id).map(CrisisEventDetailsDto::fromEntity);
    }

    /**
     * Retrieves previews of all inactive crisis events.
     *
     * @param pageable pagination information
     * @return Page of inactive crisis event previews, sorted by severity
     */
    @Transactional(readOnly = true)
    public Page<CrisisEventPreviewDto> getInactiveCrisisEventPreviews(Pageable pageable) {
        List<CrisisEvent> inactiveEvents = crisisEventRepository.findByActiveFalse();
        List<CrisisEventPreviewDto> previews = inactiveEvents.stream()
            .map(CrisisEventPreviewDto::fromEntity)
            .sorted((a, b) -> Integer.compare(severityOrder(b.getSeverity()),
                severityOrder(a.getSeverity())))
            .toList();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), previews.size());
        List<CrisisEventPreviewDto> pagedList =
            (start <= end) ? previews.subList(start, end) : List.of();
        return new PageImpl<>(pagedList, pageable, previews.size());
    }

    /**
     * Searches for crisis events by name and active status.
     *
     * @param searchTerm the search term to match against event names
     * @param isActive whether to search among active or inactive events
     * @param pageable pagination information
     * @return Page of matching crisis events, sorted by severity
     */
    @Transactional(readOnly = true)
    public Page<CrisisEvent> searchCrisisEvents(String searchTerm, boolean isActive, Pageable pageable) {
        Page<CrisisEvent> allMatchingEvents = searchUtil.searchByDescription(
            CrisisEvent.class,
            "name",
            searchTerm,
            pageable
        );

        List<CrisisEvent> filteredEvents = allMatchingEvents.getContent().stream()
            .filter(event -> event.getActive() == isActive)
            .sorted((a, b) -> Integer.compare(severityOrder(b.getSeverity()), 
                severityOrder(a.getSeverity())))
            .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredEvents.size());
        List<CrisisEvent> pageContent = filteredEvents.subList(start, end);

        return new PageImpl<>(pageContent, pageable, filteredEvents.size());
    }
}