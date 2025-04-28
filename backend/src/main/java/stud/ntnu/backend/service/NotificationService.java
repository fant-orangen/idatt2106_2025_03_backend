package stud.ntnu.backend.service;

import org.slf4j.Logger;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.backend.dto.user.NotificationDto;
import stud.ntnu.backend.model.user.Notification;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.repository.user.NotificationRepository;
import stud.ntnu.backend.repository.user.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing notifications. Handles creation, retrieval, and sending of notifications.
 */
@Service
public class NotificationService {

  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;
  private final SimpMessagingTemplate messagingTemplate;
  Logger log = org.slf4j.LoggerFactory.getLogger(NotificationService.class);

  /**
   * Constructor for dependency injection.
   *
   * @param notificationRepository repository for notification operations
   * @param userRepository         repository for user operations
   * @param messagingTemplate      messaging template for WebSocket communication
   */
  public NotificationService(NotificationRepository notificationRepository,
      UserRepository userRepository,
      SimpMessagingTemplate messagingTemplate) {
    this.notificationRepository = notificationRepository;
    this.userRepository = userRepository;
    this.messagingTemplate = messagingTemplate;
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
   * @param radiusInKm radius in kilometers
   * @return true if the second point is within the radius of the first point
   */
  public boolean isWithinRadius(BigDecimal lat1, BigDecimal lon1,
      BigDecimal lat2, BigDecimal lon2,
      BigDecimal radiusInKm) {
    if (lat1 == null || lon1 == null || lat2 == null || lon2 == null || radiusInKm == null) {
      return false;
    }

    // Convert to double for calculation
    double lat1Double = lat1.doubleValue();
    double lon1Double = lon1.doubleValue();
    double lat2Double = lat2.doubleValue();
    double lon2Double = lon2.doubleValue();
    double radiusDouble = radiusInKm.doubleValue();

    // Earth's radius in kilometers
    final double EARTH_RADIUS = 6371.0;

    // Convert latitude and longitude from degrees to radians
    double lat1Rad = Math.toRadians(lat1Double);
    double lon1Rad = Math.toRadians(lon1Double);
    double lat2Rad = Math.toRadians(lat2Double);
    double lon2Rad = Math.toRadians(lon2Double);

    // Haversine formula
    double dLat = lat2Rad - lat1Rad;
    double dLon = lon2Rad - lon1Rad;
    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.cos(lat1Rad) * Math.cos(lat2Rad) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    double distance = EARTH_RADIUS * c;

    return distance <= radiusDouble;
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
}
