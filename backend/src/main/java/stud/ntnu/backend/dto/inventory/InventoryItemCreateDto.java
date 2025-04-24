package stud.ntnu.backend.dto.inventory;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for creating a new inventory item.
 */
@Setter
@Getter
public class InventoryItemCreateDto {

  // Getters and setters
  private Integer productId;
  private String customName;
  private BigDecimal quantity;
  private LocalDate expirationDate;

  // Default constructor
  public InventoryItemCreateDto() {
  }

  // Constructor with all fields
  public InventoryItemCreateDto(Integer productId, String customName, BigDecimal quantity,
      LocalDate expirationDate) {
    this.productId = productId;
    this.customName = customName;
    this.quantity = quantity;
    this.expirationDate = expirationDate;
  }

}