package stud.ntnu.backend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stud.ntnu.backend.model.user.Notification;

import java.time.LocalDateTime;

/**
 * DTO for Notification entity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {

  private Integer id;
  private Integer userId;
  private String preferenceType;
  private String targetType;
  private Integer targetId;
  private String description;
  private LocalDateTime notifyAt;
  private LocalDateTime sentAt;
  private LocalDateTime readAt;
  private LocalDateTime createdAt;

  /**
   * Converts a Notification entity to a NotificationDto.
   *
   * @param notification the notification entity
   * @return the notification DTO
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
