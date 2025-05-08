package stud.ntnu.backend.dto.household;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for inviting a user to a household.
 */
@Setter
@Getter
public class HouseholdInviteRequestDto {

  @Email(message = "Invalid email format")
  private String email;

  // Default constructor
  public HouseholdInviteRequestDto() {
  }

  // Constructor with all fields
  public HouseholdInviteRequestDto(String email) {
    this.email = email;
  }

}