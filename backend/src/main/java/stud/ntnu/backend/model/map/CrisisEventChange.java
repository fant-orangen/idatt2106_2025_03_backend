package stud.ntnu.backend.model.map;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stud.ntnu.backend.model.user.User;

import java.time.LocalDateTime;

/**
 * Entity representing a change to a crisis event.
 */
@Entity
@Table(name = "crisis_event_changes")
@Getter
@Setter
@NoArgsConstructor
public class CrisisEventChange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "crisis_event_id", nullable = false)
    private CrisisEvent crisisEvent;

    @Enumerated(EnumType.STRING)
    @Column(name = "change_type", nullable = false)
    private ChangeType changeType;

    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdByUser;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Enum representing the type of change made to a crisis event.
     */
    public enum ChangeType {
        creation, level_change, description_update, epicenter_moved
    }

    /**
     * Constructor for creating a new crisis event change.
     *
     * @param crisisEvent   the crisis event that was changed
     * @param changeType    the type of change
     * @param oldValue      the old value
     * @param newValue      the new value
     * @param createdByUser the user who made the change
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

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
