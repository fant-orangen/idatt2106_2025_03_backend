package stud.ntnu.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for updating an existing inventory item.
 */
@Setter
@Getter
public class InventoryItemUpdateDto {
  // Getters and setters
  private Integer productId;
    private String customName;
    private BigDecimal quantity;
    private LocalDate expirationDate;
    
    // Default constructor
    public InventoryItemUpdateDto() {
    }
    
    // Constructor with all fields
    public InventoryItemUpdateDto(Integer productId, String customName, BigDecimal quantity, LocalDate expirationDate) {
        this.productId = productId;
        this.customName = customName;
        this.quantity = quantity;
        this.expirationDate = expirationDate;
    }

}