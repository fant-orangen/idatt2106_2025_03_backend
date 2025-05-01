package stud.ntnu.backend.dto.inventory;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for creating a new Water ProductType.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaterProductTypeCreateDto {

  private Integer householdId;

  @NotBlank(message = "Name is required")
  private String name;

  @NotBlank(message = "Unit is required")
  private String unit;

  private String category = "water";

  @AssertTrue(message = "Only unit 'l' (liter) is allowed for water products.")
  private boolean isUnitValid() {
    return "l".equalsIgnoreCase(unit);
  }

  public void setHouseholdId(Integer householdId) {
    this.householdId = householdId;
  }
} 