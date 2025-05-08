package stud.ntnu.backend.dto.inventory;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for creating a new ProductBatch.
 * This class represents the data structure required to create a new batch of products,
 * including the product type, quantity, and optional expiration time.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductBatchCreateDto {

    /**
     * The unique identifier of the product type for this batch.
     * Must not be null.
     */
    @NotNull(message = "Product type ID is required")
    private Integer productTypeId;

    /**
     * The number of units in this batch.
     * Must be a positive number and not null.
     */
    @NotNull(message = "Number of units is required")
    @Positive(message = "Number of units must be positive")
    private Integer number;

    /**
     * The expiration time for this batch of products.
     * This field is optional and may be null if the products don't expire.
     */
    private LocalDateTime expirationTime;
}