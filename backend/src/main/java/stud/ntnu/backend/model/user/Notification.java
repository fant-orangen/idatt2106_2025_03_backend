package stud.ntnu.backend.model.user;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a notification in the system. This entity stores information about various types of
 * notifications including their target, timing, and delivery status.
 */
@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
public class Notification {

  /**
   * Unique identifier for the notification.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /**
   * The user who will receive this notification.
   */
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  /**
   * The type of notification preference.
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "preference_type", nullable = false)
  private PreferenceType preferenceType;

  /**
   * The type of target this notification is associated with. Nullable for system notifications.
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "target_type", nullable = true)
  private TargetType targetType;

  /**
   * The ID of the target entity this notification is associated with.
   */
  @Column(name = "target_id")
  private Integer targetId;

  /**
   * Detailed description of the notification. Stored as TEXT in the database.
   */
  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  /**
   * The scheduled time when this notification should be sent.
   */
  @Column(name = "notify_at", nullable = false)
  private LocalDateTime notifyAt;

  /**
   * The timestamp when this notification was actually sent.
   */
  @Column(name = "sent_at")
  private LocalDateTime sentAt;

  /**
   * The timestamp when this notification was read by the user.
   */
  @Column(name = "read_at")
  private LocalDateTime readAt;

  /**
   * The timestamp when this notification was created. This field cannot be updated after creation.
   */
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /**
   * Sets the creation timestamp before persisting the entity.
   */
  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }

  /**
   * Enum representing different types of notification preferences.
   */
  public enum PreferenceType {
    /**
     * Reminder for expiring items
     */
    expiration_reminder,
    /**
     * Alert for low remaining supplies
     */
    remaining_supply_alert,
    /**
     * Alert for crisis events
     */
    crisis_alert,
    /**
     * Request for location information
     */
    location_request,
    /**
     * System-wide notification
     */
    system,
    /**
     * Request for safety confirmation
     */
    safety_request
  }

  /**
   * Enum representing different types of notification targets.
   */
  public enum TargetType {
    /**
     * Notification related to inventory
     */
    inventory,
    /**
     * Notification related to events
     */
    event,
    /**
     * Notification related to location requests
     */
    location_request,
    /**
     * Notification related to invitations
     */
    invitation
  }

  /**
   * Constructs a new notification with the required fields.
   *
   * @param user           The user who will receive the notification
   * @param preferenceType The type of notification preference
   * @param targetType     The type of target this notification is associated with
   * @param notifyAt       The scheduled time for the notification
   */
  public Notification(User user, PreferenceType preferenceType, TargetType targetType,
      LocalDateTime notifyAt) {
    this.user = user;
    this.preferenceType = preferenceType;
    this.targetType = targetType;
    this.notifyAt = notifyAt;
  }

  /**
   * Constructs a new system notification without a target type.
   *
   * @param user           The user who will receive the notification
   * @param preferenceType The type of notification preference
   * @param notifyAt       The scheduled time for the notification
   */
  public Notification(User user, PreferenceType preferenceType, LocalDateTime notifyAt) {
    this.user = user;
    this.preferenceType = preferenceType;
    this.notifyAt = notifyAt;
  }

  /**
   * Constructs a new notification with all fields.
   *
   * @param user           The user who will receive the notification
   * @param preferenceType The type of notification preference
   * @param targetType     The type of target this notification is associated with
   * @param targetId       The ID of the target entity
   * @param description    The detailed description of the notification
   * @param notifyAt       The scheduled time for the notification
   */
  public Notification(User user, PreferenceType preferenceType, TargetType targetType,
      Integer targetId, String description, LocalDateTime notifyAt) {
    this.user = user;
    this.preferenceType = preferenceType;
    this.targetType = targetType;
    this.targetId = targetId;
    this.description = description;
    this.notifyAt = notifyAt;
  }
}
