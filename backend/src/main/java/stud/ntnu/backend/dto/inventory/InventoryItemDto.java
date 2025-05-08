package stud.ntnu.backend.dto.inventory;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for returning an inventory item.
 * <p>
 * This class is used to transfer data about an inventory item when returning it from the backend.
 * It contains information about the item's ID, associated product, product type, custom name,
 * quantity, and expiration date.
 */
@Setter
@Getter
public class InventoryItemDto {

  /**
   * The unique identifier of the inventory item.
   */
  private Integer id;

  /**
   * The ID of the product associated with this inventory item.
   */
  private Integer productId;

  /**
   * The name of the product associated with this inventory item.
   */
  private String productName;

  /**
   * The name of the product type associated with this inventory item.
   */
  private String productTypeName;

  /**
   * A custom name for the inventory item, if provided by the user.
   */
  private String customName;

  /**
   * The quantity of the inventory item.
   */
  private BigDecimal quantity;

  /**
   * The expiration date of the inventory item, if applicable.
   */
  private LocalDate expirationDate;

  /**
   * Default constructor.
   */
  public InventoryItemDto() {
  }

  /**
   * Constructs a new InventoryItemDto with all fields.
   *
   * @param id              the unique identifier of the inventory item
   * @param productId       the ID of the product associated with this inventory item
   * @param productName     the name of the product
   * @param productTypeName the name of the product type
   * @param customName      a custom name for the inventory item
   * @param quantity        the quantity of the inventory item
   * @param expirationDate  the expiration date of the inventory item
   */
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