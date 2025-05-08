package stud.ntnu.backend.dto.household;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for joining a household using an invitation token.
 */
@Setter
@Getter
public class HouseholdJoinRequestDto {

  // Getters and setters
  private String token;

  // Default constructor
  public HouseholdJoinRequestDto() {
  }

  // Constructor with all fields
  public HouseholdJoinRequestDto(String token) {
    this.token = token;
  }

}