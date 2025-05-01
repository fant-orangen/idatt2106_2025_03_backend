package stud.ntnu.backend.dto.inventory;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for updating a ProductBatch.
 * <p>
 * This DTO is used for reducing the number of units in an existing batch.
 * It contains validation to ensure that the number of units to remove is provided and is positive.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductBatchUpdateDto {

    /**
     * The number of units to remove from the batch.
     * <p>
     * This field is required and must be a positive integer.
     */
    @NotNull(message = "Number of units to remove is required")
    @Positive(message = "Number of units to remove must be positive")
    private Integer unitsToRemove;
}