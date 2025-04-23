package stud.ntnu.backend.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import lombok.Getter;

@Getter
@Embeddable
public class GroupMembershipId implements Serializable {

  // Getters and Setters
  private Integer groupId;
  private Integer householdId;

  // Constructors
  public GroupMembershipId() {
  }

  public GroupMembershipId(Integer groupId, Integer householdId) {
    this.groupId = groupId;
    this.householdId = householdId;
  }

  public void setGroupId(Integer groupId) {
    this.groupId = groupId;
  }

  public void setHouseholdId(Integer householdId) {
    this.householdId = householdId;
  }

  // Equals and HashCode
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GroupMembershipId that = (GroupMembershipId) o;
    return Objects.equals(groupId, that.groupId) &&
        Objects.equals(householdId, that.householdId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(groupId, householdId);
  }
}