package stud.ntnu.backend.service.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.backend.dto.user.NotificationDto; //
import stud.ntnu.backend.model.map.CrisisEvent; //
import stud.ntnu.backend.model.user.Notification; //
import stud.ntnu.backend.model.user.User; //
import stud.ntnu.backend.repository.user.NotificationRepository; //
import stud.ntnu.backend.repository.user.UserRepository; //
import stud.ntnu.backend.util.LocationUtil; //
import stud.ntnu.backend.model.household.Household; //


import java.math.BigDecimal; //
import java.time.LocalDateTime; //
import java.time.format.DateTimeFormatter; //
import java.util.List; //
import java.util.Objects; //
import java.util.stream.Collectors; //

/**
 * Service for managing notifications. Handles creation, retrieval, and sending of notifications.
 */
@Service
public class NotificationService {

  private final NotificationRepository notificationRepository; //
  private final UserRepository userRepository; //
  private final SimpMessagingTemplate messagingTemplate;
  private final UserService userService; //
  private final Logger log = LoggerFactory.getLogger(NotificationService.class);

  /**
   * Constructs the NotificationService with necessary dependencies.
   *
   * @param notificationRepository Repository for notification data access.
   * @param userRepository         Repository for user data access.
   * @param messagingTemplate      Template for sending messages via WebSocket.
   * @param userService            Service for user-related operations.
   */
  public NotificationService(NotificationRepository notificationRepository, //
      UserRepository userRepository, //
      SimpMessagingTemplate messagingTemplate,
      UserService userService) { //
    this.notificationRepository = notificationRepository; //
    this.userRepository = userRepository; //
    this.messagingTemplate = messagingTemplate;
    this.userService = userService; //
  }

  /**
   * Creates a new notification for a user.
   *
   * @param user           The user receiving the notification.
   * @param preferenceType The type of notification preference (e.g., crisis_alert).
   * @param targetType     The type of entity the notification relates to (e.g., event).
   * @param targetId       The ID of the related entity.
   * @param description    The content/description of the notification.
   * @return The saved Notification entity.
   */
  @Transactional
  public Notification createNotification(User user, //
      Notification.PreferenceType preferenceType, //
      Notification.TargetType targetType, //
      Integer targetId,
      String description) {
    Notification notification = new Notification( //
        user, //
        preferenceType, //
        targetType, //
        targetId,
        description,
        LocalDateTime.now() //
    );
    return notificationRepository.save(notification); //
  }

  /**
   * Sends a previously created notification via WebSocket to the specific user topic. It also marks
   * the notification as sent by setting the 'sentAt' timestamp.
   *
   * @param notification The Notification object to send.
   */
  public void sendNotification(Notification notification) { //
    notification.setSentAt(LocalDateTime.now()); //
    notificationRepository.save(notification); //
    NotificationDto notificationDto = NotificationDto.fromEntity(notification); //
    String destination = "/topic/notifications/" + notification.getUser().getId(); //
    log.info("Sending notification to user {}: {}", notification.getUser().getId(),
        notificationDto.getDescription()); //
    messagingTemplate.convertAndSend(destination, notificationDto); //
  }

  /**
   * Retrieves all notifications for a specific user, ordered by notification time descending.
   *
   * @param userId The ID of the user whose notifications are to be fetched.
   * @return A List of Notification entities for the user.
   */
  @Transactional(readOnly = true)
  public List<Notification> getNotificationsForUser(Integer userId) { //
    return notificationRepository.findByUserIdOrderByNotifyAtDesc(userId); //
  }

  /**
   * Retrieves a paginated list of notifications for a specific user.
   *
   * @param userId   The ID of the user whose notifications are to be fetched.
   * @param pageable Pagination information (page number, size, sort).
   * @return A Page containing Notification entities for the user.
   */
  @Transactional(readOnly = true)
  public Page<Notification> getNotificationsForUser(Integer userId, Pageable pageable) { //
    return notificationRepository.findByUserId(userId, pageable); //
  }

  /**
   * Marks a specific notification as read by setting the 'readAt' timestamp.
   *
   * @param notificationId The ID of the notification to mark as read.
   * @return The updated Notification entity.
   * @throws IllegalStateException if the notification with the given ID is not found.
   */
  @Transactional
  public Notification markAsRead(Integer notificationId) { //
    Notification notification = notificationRepository.findById(notificationId) //
        .orElseThrow(() -> new IllegalStateException(
            "Notification not found with ID: " + notificationId)); //
    notification.setReadAt(LocalDateTime.now()); //
    return notificationRepository.save(notification); //
  }

  /**
   * Creates a system notification for all users in the database. System notifications have a
   * preference type of 'system' and no target type/ID.
   *
   * @param description   The content of the system notification.
   * @param createdByUser The admin User creating the notification.
   * @return A List of the created Notification entities (one for each user).
   */
  @Transactional
  public List<Notification> createSystemNotificationForAllUsers(String description,
      User createdByUser) { //
    List<User> allUsers = userRepository.findAll(); //
    log.info("Creating system notification '{}' for {} users.", description, allUsers.size()); //
    List<Notification> notifications = allUsers.stream() //
        .map(user -> { //
          Notification notification = new Notification(user, Notification.PreferenceType.system,
              LocalDateTime.now()); //
          notification.setDescription(description); //
          return notificationRepository.save(notification); //
        })
        .collect(Collectors.toList()); //
    log.info("Created {} system notifications.", notifications.size()); //
    return notifications; //
  }

  /**
   * Sends a list of notifications (typically system notifications) to their respective users via
   * WebSocket.
   *
   * @param notifications The list of Notification entities to send.
   */
  public void sendNotificationsToAllUsers(List<Notification> notifications) { //
    log.info("Sending {} system notifications via WebSocket.", notifications.size()); //
    for (Notification notification : notifications) { //
      sendNotification(notification); //
    }
  }

  /**
   * Sends notifications to users within a crisis event's radius when the event is CREATED. The
   * notification message includes details about the event and why the user was notified.
   *
   * @param crisisEvent the newly created crisis event.
   */
  @Transactional
  public void sendCrisisEventNotifications(CrisisEvent crisisEvent) { //
    // Determine message template based on event type (new event)
    String messageTemplate = createNewCrisisMessageTemplate(crisisEvent); //
    // Send notifications using the template and specific reason calculation
    sendCrisisEventNotificationsInternal(crisisEvent, messageTemplate, true); //
  }

  /**
   * Sends notifications about crisis event updates to users within the updated radius. It compares
   * the updated event with its previous state to determine changes and crafts an appropriate
   * message. Notifications are sent only if significant changes are detected.
   *
   * @param updatedCrisisEvent  The updated crisis event entity.
   * @param previousCrisisEvent The state of the crisis event before the update.
   */
  @Transactional
  public void sendCrisisEventUpdateNotifications(CrisisEvent updatedCrisisEvent,
      CrisisEvent previousCrisisEvent) { //
    if (updatedCrisisEvent == null || previousCrisisEvent == null) { //
      log.warn("Cannot send crisis update notifications. Event data missing."); //
      return; //
    }

    // Determine message template based on event type (update)
    String messageTemplate = createUpdateCrisisMessageTemplate(updatedCrisisEvent,
        previousCrisisEvent); //

    if (messageTemplate == null) { //
      log.info("No significant changes detected for event ID {}. No update notifications sent.",
          updatedCrisisEvent.getId()); //
      return; // No message means no significant change detected
    }

    log.info("Sending update notifications for event ID {}", updatedCrisisEvent.getId()); //
    // Send notifications using the update template and specific reason calculation
    sendCrisisEventNotificationsInternal(updatedCrisisEvent, messageTemplate, false); //
  }


  /**
   * Internal helper method to find relevant users and send notifications with tailored messages
   * based on location proximity. It checks both the user's registered home location and their
   * household's location against the crisis event's radius.
   *
   * @param crisisEvent     The relevant crisis event (new or updated).
   * @param messageTemplate A template for the notification message, containing the placeholder
   *                        "{reason}" which will be replaced.
   * @param isNewEvent      Flag indicating if this is for a new event (true) or an update (false).
   */
  @Transactional
  public void sendCrisisEventNotificationsInternal(CrisisEvent crisisEvent, String messageTemplate,
      boolean isNewEvent) { //
    if (crisisEvent == null || crisisEvent.getRadius() == null
        || crisisEvent.getEpicenterLatitude() == null
        || crisisEvent.getEpicenterLongitude() == null) { //
      log.warn("Cannot send crisis notifications. Event data incomplete: {}", crisisEvent); //
      return; //
    }

    BigDecimal eventLat = crisisEvent.getEpicenterLatitude(); //
    BigDecimal eventLon = crisisEvent.getEpicenterLongitude(); //
    // Use radius directly in meters for calculations.
    double radiusMeters = crisisEvent.getRadius().doubleValue(); //

    // Fetch ALL users.
    List<User> allUsers = userService.getAllUsers(); //
    log.info("Checking {} users against crisis event ID {}", allUsers.size(),
        crisisEvent.getId()); //

    int sentCount = 0; //
    for (User user : allUsers) { //
      String notificationReason = null; //
      boolean userHomeAffected = false; //
      boolean householdAffected = false; //

      // 1. Check user's home location (if available)
      if (user.getHomeLatitude() != null && user.getHomeLongitude() != null) { //
        double distanceToUserHome = LocationUtil.calculateDistance( //
            eventLat.doubleValue(), eventLon.doubleValue(), //
            user.getHomeLatitude().doubleValue(), user.getHomeLongitude().doubleValue() //
        );
        if (distanceToUserHome <= radiusMeters) { //
          userHomeAffected = true; //
          log.debug("User ID {}'s home location is within radius ({}m <= {}m).", user.getId(),
              distanceToUserHome, radiusMeters); //
        }
      }

      // 2. Check household location (if available)
      Household household = user.getHousehold(); // Get household directly from user object
      if (household != null && household.getLatitude() != null
          && household.getLongitude() != null) { //
        double distanceToHousehold = LocationUtil.calculateDistance( //
            eventLat.doubleValue(), eventLon.doubleValue(), //
            household.getLatitude().doubleValue(), household.getLongitude().doubleValue() //
        );
        if (distanceToHousehold <= radiusMeters) { //
          householdAffected = true; //
          log.debug("User ID {}'s household location is within radius ({}m <= {}m).", user.getId(),
              distanceToHousehold, radiusMeters); //
        }
      }

      // 3. Determine the reason text based on checks
      if (userHomeAffected && householdAffected) { //
        // Both user home and household are affected
        if (user.getHomeLatitude().equals(household.getLatitude()) && user.getHomeLongitude()
            .equals(household.getLongitude())) { //
          // If your position and household location are the same 
          notificationReason = "din posisjon/husholdningsposisjon"; //
        } else { //
          notificationReason = "både din posisjon og din husholdnings posisjon"; //
        }
      } else if (householdAffected) { //
        // ONLY household is affected
        notificationReason = "din husholdnings posisjon"; //
      } else if (userHomeAffected) { //
        // ONLY your location is affected
        notificationReason = "din posisjon"; //
      }

      // 4. Only proceed if a reason was determined
      if (notificationReason != null) { //
        // Format the final message using the template and reason
        String finalMessage = messageTemplate.replace("{reason}", notificationReason); //

        log.debug("Creating crisis_alert notification for user ID {} with reason: {}", user.getId(),
            notificationReason); //
        Notification notification = createNotification( //
            user, //
            Notification.PreferenceType.crisis_alert, //
            Notification.TargetType.event, //
            crisisEvent.getId(), //
            finalMessage // Use the specific message
        );

        sendNotification(notification); // Send the notification via WebSocket
        sentCount++; //
      } else { //
        log.trace("User ID {} is outside the radius based on stored home/household locations.",
            user.getId()); //
      }
    }
    log.info("Sent {} notifications based on stored locations for event ID {}", sentCount,
        crisisEvent.getId()); //
  }

  /**
   * Creates the message template for a newly created crisis event notification. Includes
   * placeholders for the reason the user is notified.
   *
   * @param crisisEvent The newly created CrisisEvent.
   * @return A formatted string template for the notification message.
   */
  private String createNewCrisisMessageTemplate(CrisisEvent crisisEvent) { //
    StringBuilder message = new StringBuilder("🚨 Kriselarsel: "); //
    message.append(String.format("'%s' (%s alvorlighetsgrad)", //
        crisisEvent.getName(), //
        translateSeverity(crisisEvent.getSeverity()))); //

    // Add the reason placeholder
    message.append(
        ". Du varsles fordi {reason} er innenfor faresonen"); // Ensure placeholder is here

    if (crisisEvent.getDescription() != null && !crisisEvent.getDescription().trim().isEmpty()) { //
      message.append(". Beskrivelse: ")
          .append(truncateDescription(crisisEvent.getDescription())); //
    }
    String startTimeFormatted = crisisEvent.getStartTime() != null //
        ? crisisEvent.getStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) //
        : "ukjent tidspunkt"; //
    message.append(String.format(". Startet %s.", startTimeFormatted)); //

    return message.toString(); //
  }

  /**
   * Creates the message template for a crisis event update notification. Summarizes the changes
   * between the previous and updated event states. Includes placeholders for the reason the user is
   * notified. Returns null if no significant changes are detected.
   *
   * @param updatedCrisisEvent  The updated CrisisEvent entity.
   * @param previousCrisisEvent The CrisisEvent entity representing the state before the update.
   * @return A formatted string template for the update notification message, or null if no
   * significant changes occurred.
   */
  private String createUpdateCrisisMessageTemplate(CrisisEvent updatedCrisisEvent,
      CrisisEvent previousCrisisEvent) { //
    StringBuilder changes = new StringBuilder(); //
    boolean changed = false; //
    // Check for changes
    if (!Objects.equals(updatedCrisisEvent.getName(), previousCrisisEvent.getName())) { //
      changes.append(String.format("Navn endret til '%s'. ", updatedCrisisEvent.getName())); //
      changed = true; //
    }
    if (!Objects.equals(updatedCrisisEvent.getDescription(),
        previousCrisisEvent.getDescription())) { //
      changes.append("Beskrivelse oppdatert. "); //
      changed = true; //
    }
    if (!Objects.equals(updatedCrisisEvent.getSeverity(), previousCrisisEvent.getSeverity())) { //
      changes.append(String.format("Alvorlighetsgrad endret til %s. ",
          translateSeverity(updatedCrisisEvent.getSeverity()))); //
      changed = true; //
    }
    if (locationChanged(updatedCrisisEvent.getEpicenterLatitude(),
        previousCrisisEvent.getEpicenterLatitude()) || //
        locationChanged(updatedCrisisEvent.getEpicenterLongitude(),
            previousCrisisEvent.getEpicenterLongitude())) { //
      changes.append("Posisjon oppdatert. "); //
      changed = true; //
    }
    if (!Objects.equals(updatedCrisisEvent.getRadius(), previousCrisisEvent.getRadius())) { //
      changes.append(String.format("Radius endret til %s meter. ",
          updatedCrisisEvent.getRadius() != null ? updatedCrisisEvent.getRadius().toString()
              : "ukjent")); // Assuming radius is in meters now
      changed = true; //
    }
    if (!Objects.equals(updatedCrisisEvent.getActive(), previousCrisisEvent.getActive())) { //
      changes.append(updatedCrisisEvent.getActive() ? "Hendelsen er nå aktiv igjen. "
          : "Hendelsen er nå markert som inaktiv. "); //
      changed = true; //
    }

    if (!changed) { //
      return null; // No significant changes detected
    }

    // Construct the template including the reason placeholder
    return String.format(
        "🔄 Oppdatering for '%s': %s Du varsles fordi {reason} er innenfor det berørte området.", //
        updatedCrisisEvent.getName(), changes.toString().trim()); // Ensure placeholder is here
  }

  /**
   * Translates the severity enum to a human-readable Norwegian string.
   *
   * @param severity The Severity enum value.
   * @return A Norwegian string representing the severity ("høy", "middels", "lav", "ukjent").
   */
  private String translateSeverity(CrisisEvent.Severity severity) { //
    if (severity == null) {
      return "ukjent"; //
    }
    switch (severity) { //
      case red:
        return "høy"; //
      case yellow:
        return "middels"; //
      case green:
        return "lav"; //
      default:
        return severity.name(); //
    }
  }

  /**
   * Truncates a description string to a maximum length, adding ellipsis if truncated.
   *
   * @param description The description string to truncate.
   * @return The truncated description, or the original if it's within the limit.
   */
  private String truncateDescription(String description) {
    final int MAX_LENGTH = 100; //
    if (description == null) {
      return ""; //
    }
    if (description.length() <= MAX_LENGTH) { //
      return description; //
    }
    return description.substring(0, MAX_LENGTH) + "..."; //
  }

  /**
   * Checks if two BigDecimal coordinates have changed significantly, accounting for potential
   * floating-point inaccuracies.
   *
   * @param newCoord The new coordinate value.
   * @param oldCoord The old coordinate value.
   * @return true if the coordinates are considered different, false otherwise.
   */
  private boolean locationChanged(BigDecimal newCoord, BigDecimal oldCoord) { //
    if (newCoord == null && oldCoord == null) {
      return false; //
    }
    if (newCoord == null || oldCoord == null) {
      return true; //
    }
    // Use a small tolerance for floating point comparison
    return newCoord.subtract(oldCoord).abs().compareTo(new BigDecimal("0.00001")) > 0; //
  }


}