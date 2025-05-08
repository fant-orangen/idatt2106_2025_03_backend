package stud.ntnu.backend.controller.user;

import java.security.Principal;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import stud.ntnu.backend.dto.user.NotificationDto;
import stud.ntnu.backend.dto.user.SystemNotificationCreateDto;
import stud.ntnu.backend.model.user.Notification;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.security.AdminChecker;
import stud.ntnu.backend.service.user.NotificationService;
import stud.ntnu.backend.service.user.UserService;

/**
 * REST controller for managing user notifications.
 * <p>
 * This controller handles all notification-related operations including:
 * - Retrieving paginated notifications for users
 * - Marking notifications as read (single or all)
 * - Checking for unread notifications
 * - Managing system-wide notifications (admin only)
 * - Configuring notification preferences
 * <p>
 * All endpoints require user authentication and operate on behalf of the authenticated user.
 */
@RestController
@RequestMapping("/api")
public class NotificationController {

  private final NotificationService notificationService;
  private final UserService userService;
  private final SimpMessagingTemplate messagingTemplate;

  /**
   * Constructs a new NotificationController with required dependencies.
   *
   * @param notificationService service for notification operations
   * @param userService service for user operations
   * @param messagingTemplate template for WebSocket messaging
   */
  public NotificationController(NotificationService notificationService,
      UserService userService,
      SimpMessagingTemplate messagingTemplate) {
    this.notificationService = notificationService;
    this.userService = userService;
    this.messagingTemplate = messagingTemplate;
  }

  /**
   * Retrieves paginated notifications for the current user.
   *
   * @param principal the authenticated user's principal
   * @param pageable pagination parameters (page number, size, sorting)
   * @return ResponseEntity containing a page of NotificationDto objects, or 400 Bad Request if an error occurs
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
   * Marks a specific notification as read for the current user.
   *
   * @param id the ID of the notification to mark as read
   * @param principal the authenticated user's principal
   * @return ResponseEntity containing the updated NotificationDto, 403 Forbidden if unauthorized,
   *         or 400 Bad Request if an error occurs
   */
  @PutMapping("/user/notifications/{id}/read")
  public ResponseEntity<?> markAsRead(@PathVariable Integer id, Principal principal) {
    try {
      User user = userService.getUserByEmail(principal.getName())
          .orElseThrow(() -> new IllegalStateException("User not found"));

      Notification notification = notificationService.markAsRead(id);

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
   * @param principal the authenticated user's principal
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
   * @param principal the authenticated user's principal
   * @return ResponseEntity containing a boolean indicating unread status, or 400 Bad Request if an error occurs
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
   * Sends a notification to a specific WebSocket topic.
   *
   * @param topic the WebSocket topic to send the notification to
   * @param payload the notification payload to send
   */
  public void sendNotification(String topic, Object payload) {
    messagingTemplate.convertAndSend(topic, payload);
  }

  /**
   * Creates a system-wide notification visible to all users.
   * <p>
   * This endpoint is restricted to users with ADMIN or SUPERADMIN roles.
   * The notification is created for all users and sent via WebSocket.
   *
   * @param createDto the DTO containing the notification description
   * @param principal the authenticated user's principal
   * @return ResponseEntity with status 200 OK if successful, 403 Forbidden if unauthorized,
   *         or 400 Bad Request if an error occurs
   */
  @PostMapping("/admin/notifications/system")
  public ResponseEntity<?> createSystemNotification(
      @Valid @RequestBody SystemNotificationCreateDto createDto,
      Principal principal) {
    try {
      if (!AdminChecker.isCurrentUserAdmin(principal, userService)) {
        return ResponseEntity.status(403)
            .body("Only administrators can create system notifications");
      }

      User currentUser = userService.getUserByEmail(principal.getName())
          .orElseThrow(() -> new IllegalStateException("User not found"));

      List<Notification> notifications = notificationService.createSystemNotificationForAllUsers(
          createDto.getDescription(), currentUser);

      notificationService.sendNotificationsToAllUsers(notifications);

      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Updates a user's notification preference for a specific notification type.
   *
   * @param principal the authenticated user's principal
   * @param preferenceType the type of notification preference to modify
   * @param enable whether to enable or disable the preference
   * @return ResponseEntity with status 200 OK if successful, or 400 Bad Request if an error occurs
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
