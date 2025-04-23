package stud.ntnu.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "group_memberships")
@Getter
@Setter
@NoArgsConstructor
public class GroupMembership {

  @EmbeddedId
  private GroupMembershipId id;

  @ManyToOne
  @MapsId("groupId")
  @JoinColumn(name = "group_id")
  private Group group;

  @ManyToOne
  @MapsId("householdId")
  @JoinColumn(name = "household_id")
  private Household household;

  @ManyToOne
  @JoinColumn(name = "invited_by_user_id")
  private User invitedByUser;

  @Column(name = "joined_at", nullable = false, updatable = false)
  private LocalDateTime joinedAt;

  // Set joinedAt before persist
  @PrePersist
  protected void onCreate() {
    joinedAt = LocalDateTime.now();
  }

  public GroupMembership(Group group, Household household, User invitedByUser) {
    this.id = new GroupMembershipId(group.getId(), household.getId());
    this.group = group;
    this.household = household;
    this.invitedByUser = invitedByUser;
  }
}
