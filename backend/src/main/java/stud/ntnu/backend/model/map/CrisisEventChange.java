package stud.ntnu.backend.model.map;

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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import stud.ntnu.backend.model.user.User;

/**
 * Represents a change made to a crisis event in the system.
 * This entity tracks modifications to crisis events including the type of change,
 * old and new values, and metadata about who made the change and when.
 */
@Entity
@Table(name = "crisis_event_changes")
@Getter
@Setter
@NoArgsConstructor
public class CrisisEventChange {

    /**
     * Unique identifier for the crisis event change.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The crisis event that was modified.
     */
    @ManyToOne
    @JoinColumn(name = "crisis_event_id", nullable = false)
    private CrisisEvent crisisEvent;

    /**
     * The type of change made to the crisis event.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "change_type", nullable = false)
    private ChangeType changeType;

    /**
     * The previous value before the change.
     * Stored as TEXT in the database.
     */
    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    /**
     * The new value after the change.
     * Stored as TEXT in the database.
     */
    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    /**
     * The user who made this change.
     */
    @ManyToOne
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdByUser;

    /**
     * The timestamp when this change was created.
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * The timestamp when this change was last updated.
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Enum representing the different types of changes that can be made to a crisis event.
     */
    public enum ChangeType {
        /** When a new crisis event is created */
        creation,
        /** When the severity level of a crisis event is changed */
        level_change,
        /** When the description of a crisis event is updated */
        description_update,
        /** When the epicenter location of a crisis event is moved */
        epicenter_moved
    }

    /**
     * Creates a new crisis event change record.
     *
     * @param crisisEvent The crisis event that was modified
     * @param changeType The type of change made
     * @param oldValue The value before the change
     * @param newValue The value after the change
     * @param createdByUser The user who made the change
     */
    public CrisisEventChange(CrisisEvent crisisEvent, ChangeType changeType, String oldValue, String newValue, User createdByUser) {
        this.crisisEvent = crisisEvent;
        this.changeType = changeType;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.createdByUser = createdByUser;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Updates the last modified timestamp before any update operation.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
