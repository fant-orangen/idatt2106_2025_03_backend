package stud.ntnu.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "group_memberships")
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

  // Constructors
  public GroupMembership() {
  }

  public GroupMembership(Group group, Household household, User invitedByUser) {
    this.id = new GroupMembershipId(group.getId(), household.getId());
    this.group = group;
    this.household = household;
    this.invitedByUser = invitedByUser;
  }

  // Getters and Setters
  public GroupMembershipId getId() {
    return id;
  }

  public void setId(GroupMembershipId id) {
    this.id = id;
  }

  public Group getGroup() {
    return group;
  }

  public void setGroup(Group group) {
    this.group = group;
  }

  public Household getHousehold() {
    return household;
  }

  public void setHousehold(Household household) {
    this.household = household;
  }

  public User getInvitedByUser() {
    return invitedByUser;
  }

  public void setInvitedByUser(User invitedByUser) {
    this.invitedByUser = invitedByUser;
  }

  public LocalDateTime getJoinedAt() {
    return joinedAt;
  }

  public void setJoinedAt(LocalDateTime joinedAt) {
    this.joinedAt = joinedAt;
  }
}