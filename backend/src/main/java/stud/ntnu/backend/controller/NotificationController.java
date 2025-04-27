package stud.ntnu.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.backend.dto.user.NotificationDto;
import stud.ntnu.backend.model.user.Notification;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.service.NotificationService;
import stud.ntnu.backend.service.UserService;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for managing notifications.
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

  private final NotificationService notificationService;
  private final UserService userService;
  private final SimpMessagingTemplate messagingTemplate;

  public NotificationController(NotificationService notificationService,
      UserService userService,
      SimpMessagingTemplate messagingTemplate) {
    this.notificationService = notificationService;
    this.userService = userService;
    this.messagingTemplate = messagingTemplate;
  }

  /**
   * Gets all notifications for the current user.
   * TODO: Untested!
   * @param principal the Principal object representing the current user
   * @return ResponseEntity with a list of notifications
   */
  @GetMapping
  public ResponseEntity<?> getNotifications(Principal principal) {
    try {
      User user = userService.getUserByEmail(principal.getName())
          .orElseThrow(() -> new IllegalStateException("User not found"));

      List<Notification> notifications = notificationService.getNotificationsForUser(user.getId());
      List<NotificationDto> notificationDtos = notifications.stream()
          .map(NotificationDto::fromEntity)
          .collect(Collectors.toList());

      return ResponseEntity.ok(notificationDtos);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Marks a notification as read.
   * TODO: Untested!
   * @param id        the ID of the notification
   * @param principal the Principal object representing the current user
   * @return ResponseEntity with the updated notification
   */
  @PutMapping("/{id}/read")
  public ResponseEntity<?> markAsRead(@PathVariable Integer id, Principal principal) {
    try {
      User user = userService.getUserByEmail(principal.getName())
          .orElseThrow(() -> new IllegalStateException("User not found"));

      Notification notification = notificationService.markAsRead(id);

      // Check if the notification belongs to the current user
      if (!notification.getUser().getId().equals(user.getId())) {
        return ResponseEntity.status(403)
            .body("You don't have permission to mark this notification as read");
      }

      return ResponseEntity.ok(NotificationDto.fromEntity(notification));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Sends a notification to a specific topic.
   * TODO: Untested!
   * @param topic   the topic to send the notification to
   * @param payload the notification payload
   */
  public void sendNotification(String topic, Object payload) {
    messagingTemplate.convertAndSend(topic, payload);
  }
}
