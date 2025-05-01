package stud.ntnu.backend.dto.inventory;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for creating a new Water ProductType.
 * <p>
 * This DTO is used when adding a new water product type to the inventory.
 * It contains validation to ensure that only valid units are allowed for water products,
 * and that all required fields are present and valid.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaterProductTypeCreateDto {

  /**
   * The ID of the household to which this product type belongs.
   * This is set by the backend based on the authenticated user.
   */
  private Integer householdId;

  /**
   * The name of the water product type.
   * This field is required and must not be blank.
   */
  @NotBlank(message = "Name is required")
  private String name;

  /**
   * The unit of measurement for the water product type (must be "l").
   * This field is required and must not be blank.
   */
  @NotBlank(message = "Unit is required")
  private String unit;

  /**
   * The category of the product type.
   * This is always set to "water" for water product types.
   */
  private String category = "water";

  /**
   * Validates that the unit is "l", which is the only allowed unit for water products.
   *
   * @return true if the unit is "l" (case-insensitive), false otherwise
   */
  @AssertTrue(message = "Only unit 'l' (liter) is allowed for water products.")
  private boolean isUnitValid() {
    return "l".equalsIgnoreCase(unit);
  }

  /**
   * Sets the household ID for this DTO.
   * This is typically set by the backend based on the authenticated user.
   *
   * @param householdId the household ID to set
   */
  public void setHouseholdId(Integer householdId) {
    this.householdId = householdId;
  }
} 