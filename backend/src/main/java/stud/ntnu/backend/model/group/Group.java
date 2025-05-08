package stud.ntnu.backend.model.group;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stud.ntnu.backend.model.user.User;

/**
 * Represents a group entity in the system.
 * Groups can be created by users and have different statuses (active/archived).
 */
@Entity
@Table(name = "groups")
@Getter
@Setter
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Group {

    /**
     * Unique identifier for the group.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Name of the group.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * User who created the group.
     */
    @ManyToOne
    @JoinColumn(name = "created_by_user_id", nullable = false)
    @JsonBackReference
    private User createdByUser;

    /**
     * Timestamp when the group was created.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Current status of the group.
     * Defaults to active when created.
     */
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private GroupStatus status = GroupStatus.active;

    /**
     * Sets the creation timestamp before persisting the entity.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * Creates a new group with the specified name and creator.
     *
     * @param name The name of the group
     * @param createdByUser The user creating the group
     */
    public Group(String name, User createdByUser) {
        this.name = name;
        this.createdByUser = createdByUser;
    }

    /**
     * Represents the possible states a group can be in.
     */
    public enum GroupStatus {
        /** Group is currently active and can be used */
        active,
        /** Group has been archived and is no longer active */
        archived
    }
}
