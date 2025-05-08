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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing a user's notification preference settings.
 * This class stores the user's preferences for different types of notifications
 * and tracks when these preferences were created and last updated.
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notification_preferences")
public class NotificationPreference {
    /**
     * Unique identifier for the notification preference.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The user associated with these notification preferences.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * The type of notification this preference applies to.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "preference_type", nullable = false)
    private Notification.PreferenceType preferenceType;

    /**
     * Whether this notification type is enabled for the user.
     * Defaults to true.
     */
    @Column(nullable = false)
    private boolean enabled = true;

    /**
     * Timestamp when this preference was created.
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when this preference was last updated.
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Sets the creation and update timestamps when the entity is first persisted.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Updates the timestamp whenever the entity is modified.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Creates a new notification preference for a user.
     *
     * @param user The user to create the preference for
     * @param preferenceType The type of notification preference to create
     */
    public NotificationPreference(User user, Notification.PreferenceType preferenceType) {
        this.user = user;
        this.preferenceType = preferenceType;
        this.enabled = true;
        onCreate();
    }
}
