package stud.ntnu.backend.dto.inventory;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for creating a new food ProductType.
 * <p>
 * This DTO is used when adding a new food product type to the inventory. It contains validation to
 * ensure that only valid units are allowed for food products, and that all required fields are
 * present and valid.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodProductTypeCreateDto {

  /**
   * The ID of the household to which this product type belongs. This is set by the backend based on
   * the authenticated user.
   */
  private Integer householdId;

  /**
   * The name of the food product type. This field is required and must not be blank.
   */
  @NotBlank(message = "Name is required")
  private String name;

  /**
   * The unit of measurement for the food product type (e.g., "kg", "l", "stk"). This field is
   * required and must not be blank.
   */
  @NotBlank(message = "Unit is required")
  private String unit;

  /**
   * The number of calories per unit for the food product type. This field is required and must be
   * zero or positive.
   */
  @NotNull(message = "Calories per unit is required")
  @PositiveOrZero(message = "Calories per unit must be positive or zero")
  private Double caloriesPerUnit;

  /**
   * The category of the product type. This is always set to "food" for food product types.
   */
  private String category = "food";

  /**
   * Validates that the unit is not "dose" or "mcg", which are not allowed for food products.
   *
   * @return true if the unit is valid for food products, false otherwise
   */
  @AssertTrue(message = "Units 'dose' and 'mcg' are not allowed for food products.")
  private boolean isUnitValid() {
    String u = unit == null ? "" : unit.toLowerCase();
    return !"dose".equals(u) && !"mcg".equals(u);
  }

  /**
   * Sets the household ID for this DTO. This is typically set by the backend based on the
   * authenticated user.
   *
   * @param householdId the household ID to set
   */
  public void setHouseholdId(Integer householdId) {
    this.householdId = householdId;
  }
}
