package stud.ntnu.backend.dto.inventory;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for updating an existing inventory item.
 * <p>
 * This class is used to transfer data when updating an item in the inventory.
 * It contains information about the product, custom name, quantity, and expiration date.
 */
@Setter
@Getter
public class InventoryItemUpdateDto {

    /**
     * The ID of the product associated with this inventory item.
     */
    private Integer productId;

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
    public InventoryItemUpdateDto() {
    }

    /**
     * Constructs a new InventoryItemUpdateDto with all fields.
     *
     * @param productId      the ID of the product
     * @param customName     the custom name for the item
     * @param quantity       the quantity of the item
     * @param expirationDate the expiration date of the item
     */
    public InventoryItemUpdateDto(Integer productId, String customName, BigDecimal quantity, LocalDate expirationDate) {
        this.productId = productId;
        this.customName = customName;
        this.quantity = quantity;
        this.expirationDate = expirationDate;
    }

}