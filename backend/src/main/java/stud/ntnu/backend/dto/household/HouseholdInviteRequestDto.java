package stud.ntnu.backend.dto.household;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for inviting a user to a household.
 */
@Setter
@Getter
public class HouseholdInviteRequestDto {

  // Getters and setters
  private String email;

  // Default constructor
  public HouseholdInviteRequestDto() {
  }

  // Constructor with all fields
  public HouseholdInviteRequestDto(String email) {
    this.email = email;
  }

}