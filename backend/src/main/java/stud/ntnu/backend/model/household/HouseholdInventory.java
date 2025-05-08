package stud.ntnu.backend.model.household;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import stud.ntnu.backend.model.inventory.Product;

/**
 * Represents an inventory item belonging to a household.
 * This entity tracks products or custom items stored in a household's inventory,
 * including their quantities and expiration dates.
 */
@Entity
@Table(name = "household_inventory")
@Getter
@Setter
@NoArgsConstructor
public class HouseholdInventory {

    /**
     * Unique identifier for the inventory item.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The household that owns this inventory item.
     */
    @ManyToOne
    @JoinColumn(name = "household_id", nullable = false)
    private Household household;

    /**
     * The product associated with this inventory item.
     * Can be null if this is a custom item.
     */
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    /**
     * Custom name for the inventory item.
     * Used when no product is associated.
     */
    @Column(name = "custom_name")
    private String customName;

    /**
     * The quantity of the item in the inventory.
     * Stored with 2 decimal places precision.
     */
    @Column(name = "quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;

    /**
     * The expiration date of the inventory item.
     * Optional field.
     */
    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    /**
     * Timestamp when this inventory item was created.
     * Cannot be updated after creation.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when this inventory item was last updated.
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Sets the creation and update timestamps when a new inventory item is created.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Updates the timestamp when an inventory item is modified.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Creates a new inventory item with a product.
     *
     * @param household The household that owns this item
     * @param product The product to be stored
     * @param quantity The quantity of the product
     */
    public HouseholdInventory(Household household, Product product, BigDecimal quantity) {
        this.household = household;
        this.product = product;
        this.quantity = quantity;
    }

    /**
     * Creates a new inventory item with a custom name.
     *
     * @param household The household that owns this item
     * @param customName The custom name for the item
     * @param quantity The quantity of the item
     */
    public HouseholdInventory(Household household, String customName, BigDecimal quantity) {
        this.household = household;
        this.customName = customName;
        this.quantity = quantity;
    }
}
