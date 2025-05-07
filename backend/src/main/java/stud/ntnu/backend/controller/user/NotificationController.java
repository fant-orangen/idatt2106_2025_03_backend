package stud.ntnu.backend.controller.user;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.backend.dto.user.NotificationDto;
import stud.ntnu.backend.dto.user.SystemNotificationCreateDto;
import stud.ntnu.backend.model.user.Notification;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.security.AdminChecker;
import stud.ntnu.backend.service.user.NotificationService;
import stud.ntnu.backend.service.user.UserService;

import java.security.Principal;
import java.util.List;

/**
 * Controller for managing notifications.
 */
@RestController
@RequestMapping("/api")
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
   * Gets notifications for the current user in a paginated format.
   *
   * @param principal the Principal object representing the current user
   * @param pageable  pagination information
   * @return ResponseEntity with a page of notifications
   */
  @GetMapping("/user/notifications")
  public ResponseEntity<?> getNotifications(Principal principal, Pageable pageable) {
    try {
      User user = userService.getUserByEmail(principal.getName())
          .orElseThrow(() -> new IllegalStateException("User not found"));

      Page<Notification> notificationsPage = notificationService.getNotificationsForUser(
          user.getId(), pageable);
      Page<NotificationDto> notificationDtosPage = notificationsPage.map(
          NotificationDto::fromEntity);

      return ResponseEntity.ok(notificationDtosPage);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Marks a notification as read.
   * TODO: Untested!
   *
   * @param id        the ID of the notification
   * @param principal the Principal object representing the current user
   * @return ResponseEntity with the updated notification
   */
  @PutMapping("/user/notifications/{id}/read")
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
   * Marks all unread notifications as read for the current user.
   *
   * @param principal the Principal object representing the current user
   * @return ResponseEntity with status 200 OK if successful, or 400 Bad Request if an error occurs
   */
  @PatchMapping("/user/notifications/read-all") 
  public ResponseEntity<?> markAllAsRead(Principal principal) {
    try {
      String email = principal.getName();
      notificationService.markAllNotificationsAsRead(email);
      return ResponseEntity.ok().build();
    } catch(Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Checks if the current user has any unread notifications.
   * 
   * @param principal the Principal object representing the current user
   * @return ResponseEntity with status 200 OK if successful, or 400 Bad Request if an error occurs
   */
  @GetMapping("/user/notifications/any-unread")
  public ResponseEntity<?> anyUnread(Principal principal) {
    try {
      String email = principal.getName();
      boolean hasUnread = notificationService.hasUnreadNotifications(email);
      return ResponseEntity.ok(hasUnread);
    } catch(Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Sends a notification to a specific topic.
   * TODO: Untested!
   *
   * @param topic   the topic to send the notification to
   * @param payload the notification payload
   */
  public void sendNotification(String topic, Object payload) {
    messagingTemplate.convertAndSend(topic, payload);
  }

  /**
   * Creates a system notification for all users. Only users with ADMIN or SUPERADMIN roles can
   * create system notifications.
   * TODO: Untested!
   *
   * @param createDto the DTO containing the notification description
   * @param principal the Principal object representing the current user
   * @return ResponseEntity with status 200 OK if successful, or 403 Forbidden if unauthorized
   */
  @PostMapping("/admin/notifications/system")
  public ResponseEntity<?> createSystemNotification(
      @Valid @RequestBody SystemNotificationCreateDto createDto,
      Principal principal) {
    try {
      // Check if the current user is an admin
      if (!AdminChecker.isCurrentUserAdmin(principal, userService)) {
        return ResponseEntity.status(403)
            .body("Only administrators can create system notifications");
      }

      // Get the current user
      User currentUser = userService.getUserByEmail(principal.getName())
          .orElseThrow(() -> new IllegalStateException("User not found"));

      // Create system notifications for all users. TODO: make this more efficient. What if 100 000 users?
      List<Notification> notifications = notificationService.createSystemNotificationForAllUsers(
          createDto.getDescription(), currentUser);

      // Send the notifications via WebSocket
      notificationService.sendNotificationsToAllUsers(notifications);

      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Changes a user's notification preference for a specific type.
   *
   * @param principal the Principal object representing the current user
   * @param preferenceType the type of notification preference to change
   * @param enable whether to enable or disable the preference
   * @return ResponseEntity with status 200 OK if successful, or 400 Bad Request if there's an error
   */
  @PatchMapping("/user/notifications/preferences/{preferenceType}")
  public ResponseEntity<?> changeNotificationPreference(
      Principal principal,
      @PathVariable String preferenceType,
      @RequestParam boolean enable) {
    try {
      String email = principal.getName();
      User currentUser = userService.getUserByEmail(email)
          .orElseThrow(() -> new IllegalStateException("User not found"));
      
      notificationService.changeNotificationPreference(currentUser, preferenceType, enable);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
  
}
