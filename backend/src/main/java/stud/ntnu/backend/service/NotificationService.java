package stud.ntnu.backend.service;

import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.backend.dto.user.NotificationDto;
import stud.ntnu.backend.model.map.CrisisEvent;
import stud.ntnu.backend.model.user.Notification;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.repository.user.NotificationRepository;
import stud.ntnu.backend.repository.user.UserRepository;
import stud.ntnu.backend.util.LocationUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing notifications. Handles creation, retrieval, and sending of notifications.
 */
@Service
public class NotificationService {

  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;
  private final SimpMessagingTemplate messagingTemplate;
  private final UserService userService;
  Logger log = org.slf4j.LoggerFactory.getLogger(NotificationService.class);

  /**
   * Constructor for dependency injection.
   *
   * @param notificationRepository repository for notification operations
   * @param userRepository         repository for user operations
   * @param messagingTemplate      messaging template for WebSocket communication
   * @param userService           service for user operations
   */
  public NotificationService(NotificationRepository notificationRepository,
      UserRepository userRepository,
      SimpMessagingTemplate messagingTemplate,
      UserService userService) {
    this.notificationRepository = notificationRepository;
    this.userRepository = userRepository;
    this.messagingTemplate = messagingTemplate;
    this.userService = userService;
  }

  /**
   * Creates a notification for a user.
   *
   * @param user           the user to notify
   * @param preferenceType the type of notification preference
   * @param targetType     the type of target
   * @param targetId       the ID of the target (optional)
   * @param description    the description of the notification (optional)
   * @return the created notification
   */
  @Transactional
  public Notification createNotification(User user,
      Notification.PreferenceType preferenceType,
      Notification.TargetType targetType,
      Integer targetId,
      String description) {
    Notification notification = new Notification(
        user,
        preferenceType,
        targetType,
        targetId,
        description,
        LocalDateTime.now()
    );

    return notificationRepository.save(notification);
  }

  /**
   * Sends a notification to a user via WebSocket.
   *
   * @param notification the notification to send
   */
  public void sendNotification(Notification notification) {
    // Mark the notification as sent
    notification.setSentAt(LocalDateTime.now());
    notificationRepository.save(notification);

    // Convert to DTO and send via WebSocket
    NotificationDto notificationDto = NotificationDto.fromEntity(notification);
    String destination = "/topic/notifications/" + notification.getUser().getId();
    log.info("Sending notification to user: {}", notification.getUser().getId());
    messagingTemplate.convertAndSend(destination, notificationDto);
  }

  /**
   * Retrieves all notifications for a user.
   *
   * @param userId the ID of the user
   * @return a list of notifications
   */
  @Transactional(readOnly = true)
  public List<Notification> getNotificationsForUser(Integer userId) {
    return notificationRepository.findByUserId(userId);
  }

  /**
   * Retrieves notifications for a user with pagination.
   *
   * @param userId the ID of the user
   * @param pageable pagination information
   * @return a page of notifications
   */
  @Transactional(readOnly = true)
  public Page<Notification> getNotificationsForUser(Integer userId, Pageable pageable) {
    return notificationRepository.findByUserId(userId, pageable);
  }

  /**
   * Marks a notification as read.
   *
   * @param notificationId the ID of the notification
   * @return the updated notification
   * @throws IllegalStateException if the notification is not found
   */
  @Transactional
  public Notification markAsRead(Integer notificationId) {
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new IllegalStateException("Notification not found"));

    notification.setReadAt(LocalDateTime.now());
    return notificationRepository.save(notification);
  }

  /**
   * Calculates if a point is within a radius of another point.
   *
   * @param lat1       latitude of the first point
   * @param lon1       longitude of the first point
   * @param lat2       latitude of the second point
   * @param lon2       longitude of the second point
   * @param radiusInMeters radius in meters
   * @return true if the second point is within the radius of the first point
   */
  public boolean isWithinRadius(BigDecimal lat1, BigDecimal lon1,
      BigDecimal lat2, BigDecimal lon2,
      BigDecimal radiusInMeters) {
    if (lat1 == null || lon1 == null || lat2 == null || lon2 == null || radiusInMeters == null) {
      return false;
    }

    double distance = LocationUtil.calculateDistance(
        lat1.doubleValue(),
        lon1.doubleValue(),
        lat2.doubleValue(),
        lon2.doubleValue()
    );

    return distance <= radiusInMeters.doubleValue();
  }

  /**
   * Creates a system notification for all users.
   *
   * @param description the description of the notification
   * @param createdByUser the user who created the notification
   * @return a list of created notifications
   */
  @Transactional
  public List<Notification> createSystemNotificationForAllUsers(String description, User createdByUser) {
    List<User> allUsers = userRepository.findAll();

    // Create a notification for each user
    List<Notification> notifications = allUsers.stream()
        .map(user -> {
          // For system notifications, we need to set a target type even though it's not used
          // We'll use a special constructor that sets the required fields
          Notification notification = new Notification(user, Notification.PreferenceType.system, LocalDateTime.now());
          notification.setDescription(description);
          return notificationRepository.save(notification);
        })
        .collect(Collectors.toList());

    return notifications;
  }

  /**
   * Sends notifications to all users via WebSocket.
   *
   * @param notifications the list of notifications to send
   */
  public void sendNotificationsToAllUsers(List<Notification> notifications) {
    for (Notification notification : notifications) {
      sendNotification(notification);
    }
  }

  /**
   * Finds all users within a radius of a point.
   *
   * @param latitude   latitude of the center point
   * @param longitude  longitude of the center point
   * @param radiusInKm radius in kilometers
   * @return a list of users within the radius
   */
  @Transactional(readOnly = true)
  public List<User> findUsersWithinRadius(BigDecimal latitude, BigDecimal longitude,
      BigDecimal radiusInKm) {
    List<User> allUsers = userRepository.findAll();

    return allUsers.stream()
        .filter(user -> {
          // Check if user's home coordinates are within radius
          boolean userInRadius =
              user.getHomeLatitude() != null && user.getHomeLongitude() != null &&
                  isWithinRadius(latitude, longitude, user.getHomeLatitude(),
                      user.getHomeLongitude(), radiusInKm);

          // Check if user's household coordinates are within radius
          boolean householdInRadius = user.getHousehold() != null &&
              user.getHousehold().getLatitude() != null &&
              user.getHousehold().getLongitude() != null &&
              isWithinRadius(latitude, longitude, user.getHousehold().getLatitude(),
                  user.getHousehold().getLongitude(), radiusInKm);

          return userInRadius || householdInRadius;
        })
        .toList();
  }

  /**
   * Sends notifications to users within a crisis event's radius.
   *
   * @param crisisEvent the crisis event
   * @param message the notification message
   */
  @Transactional
  public void sendCrisisEventNotifications(CrisisEvent crisisEvent, String message) {
    if (crisisEvent.getRadius() != null) {
      List<User> usersInRadius = LocationUtil.findUsersWithinRadius(
          userService,
          crisisEvent.getEpicenterLatitude().doubleValue(),
          crisisEvent.getEpicenterLongitude().doubleValue(),
          crisisEvent.getRadius().doubleValue()
      );
      log.info("Found {} users in radius", usersInRadius.size());

      // Create and send notifications to users in radius
      for (User user : usersInRadius) {
        log.info("Creating notification for user: {}", user.getId()); // TODO: remove
        Notification notification = createNotification(
            user,
            Notification.PreferenceType.crisis_alert,
            Notification.TargetType.event,
            crisisEvent.getId(),
            message
        );

        // Send the notification via WebSocket
        log.info("Sending notification to user: {}", user.getId());
        sendNotification(notification);
      }
    }
  }

  /**
   * Sends notifications about crisis event updates to users within the radius.
   *
   * @param updatedCrisisEvent the updated crisis event
   * @param previousCrisisEvent the previous state of the crisis event
   */
  @Transactional
  public void sendCrisisEventUpdateNotifications(CrisisEvent updatedCrisisEvent, CrisisEvent previousCrisisEvent) {
    if (updatedCrisisEvent.getRadius() != null) {
      // Create notification message based on what changed
      StringBuilder notificationMessage = new StringBuilder("Crisis event has been updated: ");
      
      if (!updatedCrisisEvent.getName().equals(previousCrisisEvent.getName())) {
        notificationMessage.append("Name changed to ").append(updatedCrisisEvent.getName()).append(". ");
      }
      if (!updatedCrisisEvent.getDescription().equals(previousCrisisEvent.getDescription())) {
        notificationMessage.append("Description updated. ");
      }
      if (!updatedCrisisEvent.getSeverity().equals(previousCrisisEvent.getSeverity())) {
        notificationMessage.append("Severity changed to ").append(updatedCrisisEvent.getSeverity()).append(". ");
      }
      if (!updatedCrisisEvent.getEpicenterLatitude().equals(previousCrisisEvent.getEpicenterLatitude()) ||
          !updatedCrisisEvent.getEpicenterLongitude().equals(previousCrisisEvent.getEpicenterLongitude())) {
        notificationMessage.append("Location updated. ");
      }
      if (!updatedCrisisEvent.getRadius().equals(previousCrisisEvent.getRadius())) {
        notificationMessage.append("Radius changed to ").append(updatedCrisisEvent.getRadius()).append(" meters. ");
      }

      sendCrisisEventNotifications(updatedCrisisEvent, notificationMessage.toString());
    }
  }
}
