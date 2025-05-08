package stud.ntnu.backend.model.user;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

import stud.ntnu.backend.model.map.CrisisEvent;

/**
 * Entity class representing a user's reflection in the system.
 * Reflections can be associated with crisis events and can be shared or kept private.
 */
@Entity
@Table(name = "reflections")
@Getter
@Setter
@NoArgsConstructor
public class Reflection {

    /**
     * Unique identifier for the reflection.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The user who created this reflection.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * The associated crisis event, if any.
     */
    @ManyToOne
    @JoinColumn(name = "crisis_event_id")
    private CrisisEvent crisisEvent;

    /**
     * The content of the reflection.
     */
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * Indicates whether the reflection is shared with others.
     */
    @Column(name = "shared", nullable = false)
    private Boolean shared = false;

    /**
     * Indicates whether the reflection has been deleted.
     */
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    /**
     * Timestamp when the reflection was created.
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
     * Creates a new reflection with the specified user and content.
     *
     * @param user The user creating the reflection
     * @param content The content of the reflection
     */
    public Reflection(User user, String content) {
        this.user = user;
        this.content = content;
    }
}
