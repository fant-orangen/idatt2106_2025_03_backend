package stud.ntnu.backend.dto.inventory;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for creating a new Medicine ProductType.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicineProductTypeCreateDto {

  private Integer householdId;

  @NotBlank(message = "Name is required")
  private String name;

  @NotBlank(message = "Unit is required")
  private String unit;

  private String category = "medicine";

  @AssertTrue(message = "Only units 'mg', 'mcg', or 'dose' are allowed for medicine products.")
  private boolean isUnitValid() {
    String u = unit == null ? "" : unit.toLowerCase();
    return "mg".equals(u) || "mcg".equals(u) || "dose".equals(u);
  }

  public void setHouseholdId(Integer householdId) {
    this.householdId = householdId;
  }
} 