package stud.ntnu.backend.model.user;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
public class Notification {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(name = "preference_type", nullable = false)
  private PreferenceType preferenceType;

  @Enumerated(EnumType.STRING)
  @Column(name = "target_type", nullable = true) // Changed to nullable for system notifications
  private TargetType targetType;

  @Column(name = "target_id")
  private Integer targetId;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "notify_at", nullable = false)
  private LocalDateTime notifyAt;

  @Column(name = "sent_at")
  private LocalDateTime sentAt;

  @Column(name = "read_at")
  private LocalDateTime readAt;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  // Set createdAt before persist
  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }

  // Enum for preference type
  public enum PreferenceType {
    expiration_reminder, crisis_alert, location_request, system
  }

  // Enum for target type
  public enum TargetType {
    inventory, event, location_request
  }

  // Constructor with required fields
  public Notification(User user, PreferenceType preferenceType, TargetType targetType,
                     LocalDateTime notifyAt) {
    this.user = user;
    this.preferenceType = preferenceType;
    this.targetType = targetType;
    this.notifyAt = notifyAt;
  }

  // Constructor for system notifications (no target type)
  public Notification(User user, PreferenceType preferenceType, LocalDateTime notifyAt) {
    this.user = user;
    this.preferenceType = preferenceType;
    this.notifyAt = notifyAt;
  }

  // Constructor with all fields
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
