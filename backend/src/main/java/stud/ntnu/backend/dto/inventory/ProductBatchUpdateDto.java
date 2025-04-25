package stud.ntnu.backend.dto.inventory;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for updating a ProductBatch.
 * Used for reducing the number of units in an existing batch.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductBatchUpdateDto {
    @NotNull(message = "Number of units to remove is required")
    @Positive(message = "Number of units to remove must be positive")
    private Integer unitsToRemove;
}