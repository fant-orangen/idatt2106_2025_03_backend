package stud.ntnu.backend.model.inventory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a product in the inventory system.
 * This entity maps to the 'products' table in the database.
 */
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product {

    /**
     * The unique identifier for the product.
     * Automatically generated using an identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The type of product this instance represents.
     * This is a required field and cannot be null.
     */
    @ManyToOne
    @JoinColumn(name = "product_type_id", nullable = false)
    private ProductType productType;

    /**
     * The name of the product.
     * This is a required field and cannot be null.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * A detailed description of the product.
     * Stored as TEXT in the database.
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Constructs a new Product with the specified type, name, and description.
     *
     * @param productType The type of the product
     * @param name The name of the product
     * @param description The description of the product
     */
    public Product(ProductType productType, String name, String description) {
        this.productType = productType;
        this.name = name;
        this.description = description;
    }
}
