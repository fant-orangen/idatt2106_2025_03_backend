package stud.ntnu.backend.model.group;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import stud.ntnu.backend.model.household.Household;
import stud.ntnu.backend.model.user.User;

/**
 * Represents a membership relationship between a Group and a Household.
 * This entity tracks when a household joins a group and when they leave.
 */
@Entity
@Table(name = "group_memberships")
@Getter
@Setter
@NoArgsConstructor
public class GroupMembership {

    /**
     * Composite primary key for the group membership.
     */
    @EmbeddedId
    private GroupMembershipId id;

    /**
     * The group this membership belongs to.
     */
    @ManyToOne
    @MapsId("groupId")
    @JoinColumn(name = "group_id")
    private Group group;

    /**
     * The household that is a member of the group.
     */
    @ManyToOne
    @MapsId("householdId")
    @JoinColumn(name = "household_id")
    private Household household;

    /**
     * The user who invited this household to the group.
     */
    @ManyToOne
    @JoinColumn(name = "invited_by_user_id")
    private User invitedByUser;

    /**
     * Timestamp when the household joined the group.
     * This field is automatically set and cannot be updated.
     */
    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    /**
     * Timestamp when the household left the group.
     * Null if the household is still a member.
     */
    @Column(name = "left_at")
    private LocalDateTime leftAt;

    /**
     * Automatically sets the joinedAt timestamp when a new membership is created.
     */
    @PrePersist
    protected void onCreate() {
        joinedAt = LocalDateTime.now();
    }

    /**
     * Creates a new group membership.
     *
     * @param group The group to join
     * @param household The household joining the group
     * @param invitedByUser The user who invited the household
     */
    public GroupMembership(Group group, Household household, User invitedByUser) {
        this.id = new GroupMembershipId(group.getId(), household.getId());
        this.group = group;
        this.household = household;
        this.invitedByUser = invitedByUser;
    }
}
