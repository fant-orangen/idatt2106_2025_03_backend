package stud.ntnu.backend.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
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
import stud.ntnu.backend.model.user.NotificationPreference;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.security.AdminChecker;
import stud.ntnu.backend.service.user.NotificationService;
import stud.ntnu.backend.service.user.UserService;

/**
 * REST controller for managing user notifications.
 * <p>
 * This controller handles all notification-related operations including: - Retrieving paginated
 * notifications for users - Marking notifications as read (single or all) - Checking for unread
 * notifications - Managing system-wide notifications (admin only) - Configuring notification
 * preferences
 * <p>
 * All endpoints require user authentication and operate on behalf of the authenticated user.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Notifications", description = "Operations for managing user notifications and preferences")
public class NotificationController {

  private final NotificationService notificationService;
  private final UserService userService;
  private final SimpMessagingTemplate messagingTemplate;

  /**
   * Constructs a new NotificationController with required dependencies.
   *
   * @param notificationService service for notification operations
   * @param userService         service for user operations
   * @param messagingTemplate   template for WebSocket messaging
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
   * @param pageable  pagination parameters (page number, size, sorting)
   * @return ResponseEntity containing a page of NotificationDto objects, or 400 Bad Request if an
   * error occurs
   */
  @Operation(summary = "Get notifications", description = "Retrieves paginated notifications for the current user.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved notifications", 
          content = @Content(schema = @Schema(implementation = NotificationDto.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - user not found or other error", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
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
   * @param id        the ID of the notification to mark as read
   * @param principal the authenticated user's principal
   * @return ResponseEntity containing the updated NotificationDto, 403 Forbidden if unauthorized,
   * or 400 Bad Request if an error occurs
   */
  @Operation(summary = "Mark notification as read", description = "Marks a specific notification as read for the current user.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully marked notification as read", 
          content = @Content(schema = @Schema(implementation = NotificationDto.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - notification not found or other error", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "403", description = "Forbidden - user not authorized to mark this notification", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
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
  @Operation(summary = "Mark all notifications as read", description = "Marks all unread notifications as read for the current user.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully marked all notifications as read"),
      @ApiResponse(responseCode = "400", description = "Bad request - user not found or other error", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @PatchMapping("/user/notifications/read-all")
  public ResponseEntity<?> markAllAsRead(Principal principal) {
    try {
      String email = principal.getName();
      notificationService.markAllNotificationsAsRead(email);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Checks if the current user has any unread notifications.
   *
   * @param principal the authenticated user's principal
   * @return ResponseEntity containing a boolean indicating unread status, or 400 Bad Request if an
   * error occurs
   */
  @Operation(summary = "Check for unread notifications", description = "Checks if the current user has any unread notifications.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully checked unread status", 
          content = @Content(schema = @Schema(type = "boolean"))),
      @ApiResponse(responseCode = "400", description = "Bad request - user not found or other error", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @GetMapping("/user/notifications/any-unread")
  public ResponseEntity<?> anyUnread(Principal principal) {
    try {
      String email = principal.getName();
      boolean hasUnread = notificationService.hasUnreadNotifications(email);
      return ResponseEntity.ok(hasUnread);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Sends a notification to a specific WebSocket topic.
   *
   * @param topic   the WebSocket topic to send the notification to
   * @param payload the notification payload to send
   */
  public void sendNotification(String topic, Object payload) {
    messagingTemplate.convertAndSend(topic, payload);
  }

  /**
   * Creates a system-wide notification visible to all users.
   * <p>
   * This endpoint is restricted to users with ADMIN or SUPERADMIN roles. The notification is
   * created for all users and sent via WebSocket.
   *
   * @param createDto the DTO containing the notification description
   * @param principal the authenticated user's principal
   * @return ResponseEntity with status 200 OK if successful, 403 Forbidden if unauthorized, or 400
   * Bad Request if an error occurs
   */
  @Operation(summary = "Create system notification", description = "Creates a system-wide notification visible to all users. Admin only.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully created system notification"),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid notification data", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "403", description = "Forbidden - user not authorized as admin", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
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
   * @param principal      the authenticated user's principal
   * @param preferenceType the type of notification preference to modify
   * @param enable         whether to enable or disable the preference
   * @return ResponseEntity with status 200 OK if successful, or 400 Bad Request if an error occurs
   */
  @Operation(summary = "Update notification preference", description = "Updates a user's notification preference for a specific notification type.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully updated notification preference"),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid preference type or user not found", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
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

  /**
   * Retrieves the notification preferences for the current user.
   *
   * @param principal the authenticated user's principal
   */
  @GetMapping("/user/notifications/preferences")
    public ResponseEntity<?> getNotificationPreferences(Principal principal) {
        try {
        String email = principal.getName();
        User currentUser = userService.getUserByEmail(email)
            .orElseThrow(() -> new IllegalStateException("User not found"));

        List<NotificationPreference> preferences = notificationService
            .getUserNotificationPreferences(currentUser);
        return ResponseEntity.ok(preferences);
        } catch (Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
