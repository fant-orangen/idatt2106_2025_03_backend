package stud.ntnu.backend.dto.user;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stud.ntnu.backend.model.user.Notification;

/**
 * Data Transfer Object (DTO) representing a notification. This class encapsulates notification data
 * for transfer between layers of the application, including details about the notification's
 * target, timing, and status.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {

  /**
   * Unique identifier for the notification.
   */
  private Integer id;

  /**
   * Identifier of the user who owns this notification.
   */
  private Integer userId;

  /**
   * Type of preference associated with this notification.
   */
  private String preferenceType;

  /**
   * Type of target entity this notification is associated with.
   */
  private String targetType;

  /**
   * Identifier of the target entity this notification is associated with.
   */
  private Integer targetId;

  /**
   * Detailed description of the notification.
   */
  private String description;

  /**
   * Scheduled time when the notification should be delivered.
   */
  private LocalDateTime notifyAt;

  /**
   * Time when the notification was actually sent.
   */
  private LocalDateTime sentAt;

  /**
   * Time when the notification was read by the user.
   */
  private LocalDateTime readAt;

  /**
   * Time when the notification was created.
   */
  private LocalDateTime createdAt;

  /**
   * Converts a Notification entity to a NotificationDto. This method handles the conversion of
   * entity-specific data types to their DTO representations, including the conversion of enum types
   * to their string representations.
   *
   * @param notification the notification entity to convert
   * @return a new NotificationDto instance containing the converted data
   */
  public static NotificationDto fromEntity(Notification notification) {
    String targetTypeName = notification.getTargetType() != null ?
        notification.getTargetType().name() : null;

    return new NotificationDto(
        notification.getId(),
        notification.getUser().getId(),
        notification.getPreferenceType().name(),
        targetTypeName,
        notification.getTargetId(),
        notification.getDescription(),
        notification.getNotifyAt(),
        notification.getSentAt(),
        notification.getReadAt(),
        notification.getCreatedAt()
    );
  }
}
