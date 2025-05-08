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
 * This DTO is used for setting the number of units in an existing batch. It contains validation to
 * ensure that the new number of units is provided and is positive.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductBatchUpdateDto {

  /**
   * The new number of units to set for the batch.
   * <p>
   * This field is required and must be a positive integer.
   */
  @NotNull(message = "New number of units is required")
  @Positive(message = "New number of units must be positive")
  private Integer newNumberOfUnits;
}