package stud.ntnu.backend.dto.inventory;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for returning an inventory item.
 */
@Setter
@Getter
public class InventoryItemDto {

  // Getters and setters
  private Integer id;
  private Integer productId;
  private String productName;
  private String productTypeName;
  private String customName;
  private BigDecimal quantity;
  private LocalDate expirationDate;

  // Default constructor
  public InventoryItemDto() {
  }

  // Constructor with all fields
  public InventoryItemDto(Integer id, Integer productId, String productName, String productTypeName,
      String customName, BigDecimal quantity, LocalDate expirationDate) {
    this.id = id;
    this.productId = productId;
    this.productName = productName;
    this.productTypeName = productTypeName;
    this.customName = customName;
    this.quantity = quantity;
    this.expirationDate = expirationDate;
  }

}