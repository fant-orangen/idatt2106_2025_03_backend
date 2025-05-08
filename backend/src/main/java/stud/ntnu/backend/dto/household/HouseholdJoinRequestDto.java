package stud.ntnu.backend.dto.household;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for joining a household using an invitation token.
 */
@Setter
@Getter
public class HouseholdJoinRequestDto {

  @NotBlank(message = "Token is required")
  private String token;

  // Default constructor
  public HouseholdJoinRequestDto() {
  }

  // Constructor with all fields
  public HouseholdJoinRequestDto(String token) {
    this.token = token;
  }

}