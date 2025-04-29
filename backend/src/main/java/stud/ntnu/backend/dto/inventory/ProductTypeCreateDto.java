package stud.ntnu.backend.dto.inventory;

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
public class ProductTypeCreateDto {

  private Integer householdId;

  @NotBlank(message = "Name is required")
  private String name;

  @NotBlank(message = "Unit is required")
  private String unit;

  @NotNull(message = "Calories per unit is required")
  @PositiveOrZero(message = "Calories per unit must be positive or zero")
  private Double caloriesPerUnit;

  private Boolean isWater = false;
}
