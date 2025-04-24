package stud.ntnu.backend.dto.household;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for updating the population count of a household.
 */
@Setter
@Getter
public class HouseholdPopulationUpdateDto {

  // Getters and setters
  private Integer populationCount;

  // Default constructor
  public HouseholdPopulationUpdateDto() {
  }

  // Constructor with all fields
  public HouseholdPopulationUpdateDto(Integer populationCount) {
    this.populationCount = populationCount;
  }

}