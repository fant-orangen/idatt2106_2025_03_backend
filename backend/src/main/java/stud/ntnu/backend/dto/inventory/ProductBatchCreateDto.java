package stud.ntnu.backend.dto.inventory;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for creating a new ProductBatch.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductBatchCreateDto {

  @NotNull(message = "Product type ID is required")
  private Integer productTypeId;

  @NotNull(message = "Number of units is required")
  @Positive(message = "Number of units must be positive")
  private Integer number;

  private LocalDateTime expirationTime;
}