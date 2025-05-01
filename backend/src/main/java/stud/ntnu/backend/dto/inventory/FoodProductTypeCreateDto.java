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
 * Data Transfer Object for creating a new ProductType.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodProductTypeCreateDto {

  private Integer householdId;

  @NotBlank(message = "Name is required")
  private String name;

  @NotBlank(message = "Unit is required")
  private String unit;

  @NotNull(message = "Calories per unit is required")
  @PositiveOrZero(message = "Calories per unit must be positive or zero")
  private Double caloriesPerUnit;

  private String category = "food";

  @AssertTrue(message = "Units 'dose' and 'mcg' are not allowed for food products.")
  private boolean isUnitValid() {
    String u = unit == null ? "" : unit.toLowerCase();
    return !"dose".equals(u) && !"mcg".equals(u);
  }

  public void setHouseholdId(Integer householdId) {
    this.householdId = householdId;
  }
}
