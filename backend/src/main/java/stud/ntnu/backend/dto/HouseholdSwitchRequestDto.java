package stud.ntnu.backend.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for switching a user's household.
 */
@Setter
@Getter
public class HouseholdSwitchRequestDto {

  // Getters and setters
  private Integer householdId;

  // Default constructor
  public HouseholdSwitchRequestDto() {
  }

  // Constructor with all fields
  public HouseholdSwitchRequestDto(Integer householdId) {
    this.householdId = householdId;
  }

}