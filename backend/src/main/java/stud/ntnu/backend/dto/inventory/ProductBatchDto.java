package stud.ntnu.backend.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for ProductBatch. Used for transferring product batch data between layers.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductBatchDto {

  private Integer id;
  private Integer productTypeId;
  private String productTypeName;
  private LocalDateTime dateAdded;
  private LocalDateTime expirationTime;
  private Integer number;
}