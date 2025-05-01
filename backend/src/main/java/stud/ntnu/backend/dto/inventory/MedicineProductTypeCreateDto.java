package stud.ntnu.backend.dto.inventory;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for creating a new Medicine ProductType.
 * <p>
 * This DTO is used when adding a new medicine product type to the inventory.
 * It contains validation to ensure that only valid units are allowed for medicine products,
 * and that all required fields are present and valid.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicineProductTypeCreateDto {

  /**
   * The ID of the household to which this product type belongs.
   * This is set by the backend based on the authenticated user.
   */
  private Integer householdId;

  /**
   * The name of the medicine product type.
   * This field is required and must not be blank.
   */
  @NotBlank(message = "Name is required")
  private String name;

  /**
   * The unit of measurement for the medicine product type (must be "mg", "mcg", or "dose").
   * This field is required and must not be blank.
   */
  @NotBlank(message = "Unit is required")
  private String unit;

  /**
   * The category of the product type.
   * This is always set to "medicine" for medicine product types.
   */
  private String category = "medicine";

  /**
   * Validates that the unit is one of "mg", "mcg", or "dose", which are the only allowed units for medicine products.
   *
   * @return true if the unit is valid for medicine products, false otherwise
   */
  @AssertTrue(message = "Only units 'mg', 'mcg', or 'dose' are allowed for medicine products.")
  private boolean isUnitValid() {
    String u = unit == null ? "" : unit.toLowerCase();
    return "mg".equals(u) || "mcg".equals(u) || "dose".equals(u);
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