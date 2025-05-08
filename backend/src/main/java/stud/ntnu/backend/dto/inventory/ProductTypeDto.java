package stud.ntnu.backend.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing a product type in the system.
 * This class is used for transferring product type data between different layers of the application.
 * It contains essential information about a product type including its identification,
 * household association, and nutritional information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductTypeDto {

    /**
     * The unique identifier for the product type.
     */
    private Integer id;

    /**
     * The identifier of the household this product type belongs to.
     */
    private Integer householdId;

    /**
     * The name of the product type.
     */
    private String name;

    /**
     * The unit of measurement for this product type (e.g., kg, liters, pieces).
     */
    private String unit;

    /**
     * The number of calories per unit of this product type.
     */
    private Double caloriesPerUnit;

    /**
     * The category this product type belongs to (e.g., dairy, meat, vegetables).
     */
    private String category;
}
