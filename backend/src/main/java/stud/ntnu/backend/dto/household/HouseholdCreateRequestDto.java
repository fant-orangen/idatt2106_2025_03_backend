package stud.ntnu.backend.dto.household;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Data Transfer Object for household creation requests. Contains the name, address, and population
 * count for a new household.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdCreateRequestDto {

  @NotBlank(message = "Household name is required")
  private String name;

  @NotBlank(message = "Address is required")
  private String address;

  @Min(value = 1, message = "Population count must be at least 1")
  private Integer populationCount = 1;
}