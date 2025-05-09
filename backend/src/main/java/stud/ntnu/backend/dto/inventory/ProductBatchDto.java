package stud.ntnu.backend.dto.inventory;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for ProductBatch.
 * <p>
 * This class represents a batch of products in the inventory system, containing information about:
 * <ul>
 *   <li>The batch's unique identifier</li>
 *   <li>The associated product type</li>
 *   <li>When the batch was added to inventory</li>
 *   <li>When the batch expires (if applicable)</li>
 *   <li>The number of units in the batch</li>
 * </ul>
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductBatchDto {

  /**
   * The unique identifier of this product batch.
   */
  private Integer id;

  /**
   * The unique identifier of the product type associated with this batch.
   */
  private Integer productTypeId;

  /**
   * The name of the product type associated with this batch.
   */
  private String productTypeName;

  /**
   * The date and time when this batch was added to the inventory.
   */
  private LocalDateTime dateAdded;

  /**
   * The date and time when this batch expires. This may be null if the products in this batch do
   * not expire.
   */
  private LocalDateTime expirationTime;

  /**
   * The number of units in this batch.
   */
  private Integer number;
}