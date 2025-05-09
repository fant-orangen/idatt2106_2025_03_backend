package stud.ntnu.backend.model.inventory;

import java.time.LocalDateTime;

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
 * Represents a batch of products in the inventory system. Each batch is associated with a specific
 * product type and contains information about when it was added and when it expires.
 */
@Entity
@Table(name = "product_batch")
@Getter
@Setter
@NoArgsConstructor
public class ProductBatch {

  /**
   * Unique identifier for the product batch.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /**
   * The type of product this batch represents.
   */
  @ManyToOne
  @JoinColumn(name = "product_type_id", nullable = false)
  private ProductType productType;

  /**
   * The date and time when this batch was added to the inventory.
   */
  @Column(name = "date_added", nullable = false)
  private LocalDateTime dateAdded;

  /**
   * The date and time when this batch expires. Can be null if the product doesn't have an
   * expiration date.
   */
  @Column(name = "expiration_time")
  private LocalDateTime expirationTime;

  /**
   * The quantity of products in this batch.
   */
  @Column(name = "number", nullable = false)
  private Integer number;

  /**
   * Creates a new product batch with the specified details.
   *
   * @param productType    The type of product in this batch
   * @param dateAdded      The date and time when the batch was added
   * @param expirationTime The date and time when the batch expires
   * @param number         The quantity of products in the batch
   */
  public ProductBatch(ProductType productType, LocalDateTime dateAdded,
      LocalDateTime expirationTime, Integer number) {
    this.productType = productType;
    this.dateAdded = dateAdded;
    this.expirationTime = expirationTime;
    this.number = number;
  }
}