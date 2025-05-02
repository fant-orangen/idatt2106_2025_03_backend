package stud.ntnu.backend.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for ProductType.
 * Used for transferring product type data between layers.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductTypeDto {
    private Integer id;
    private Integer householdId;
    private String name;
    private String unit;
    private Double caloriesPerUnit;
    private String category;
}
