package stud.ntnu.backend.dto.household;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for household creation requests. This class represents the data
 * structure used when creating a new household, containing essential information such as name,
 * address, population count, and optional geographical coordinates.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdCreateRequestDto {

  /**
   * The name of the household. This field is required and cannot be blank.
   */
  @NotBlank(message = "Household name is required")
  private String name;

  /**
   * The physical address of the household. This field is required and cannot be blank.
   */
  @NotBlank(message = "Address is required")
  private String address;

  /**
   * The number of people living in the household. Must be at least 1, defaults to 1 if not
   * specified.
   */
  @Min(value = 1, message = "Population count must be at least 1")
  private Integer populationCount = 1;

  /**
   * The geographical latitude coordinate of the household. This field is optional.
   */
  private BigDecimal latitude;

  /**
   * The geographical longitude coordinate of the household. This field is optional.
   */
  private BigDecimal longitude;
}