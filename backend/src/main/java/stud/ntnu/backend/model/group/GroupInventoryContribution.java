package stud.ntnu.backend.model.group;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import stud.ntnu.backend.model.household.Household;
import stud.ntnu.backend.model.inventory.ProductBatch;

/**
 * Represents a contribution made by a household to a group's inventory. This entity tracks both
 * product-based and custom item contributions.
 */
@Entity
@Table(name = "group_inventory_contributions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupInventoryContribution {

  /**
   * Unique identifier for the contribution
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /**
   * The group receiving the contribution
   */
  @ManyToOne
  @JoinColumn(name = "group_id", nullable = false)
  private Group group;

  /**
   * The household making the contribution
   */
  @ManyToOne
  @JoinColumn(name = "household_id", nullable = false)
  private Household household;

  /**
   * The product being contributed (optional)
   */
  @ManyToOne
  @JoinColumn(name = "product_id")
  private ProductBatch product;

  /**
   * Custom name for non-product contributions
   */
  @Column(name = "custom_name")
  private String customName;

  /**
   * Expiration date of the contributed item
   */
  @Column(name = "expiration_date")
  private LocalDateTime expirationDate;

  /**
   * Timestamp when the contribution was made
   */
  @Column(name = "contributed_at", nullable = false)
  private LocalDateTime contributedAt;

  /**
   * Creates a new contribution with a product.
   *
   * @param group     The group receiving the contribution
   * @param household The household making the contribution
   * @param product   The product being contributed
   */
  public GroupInventoryContribution(Group group, Household household, ProductBatch product) {
    this.group = group;
    this.household = household;
    this.product = product;
    this.contributedAt = LocalDateTime.now();
  }

  /**
   * Creates a new contribution with a custom item.
   *
   * @param group      The group receiving the contribution
   * @param household  The household making the contribution
   * @param customName The name of the custom item being contributed
   */
  public GroupInventoryContribution(Group group, Household household, String customName) {
    this.group = group;
    this.household = household;
    this.customName = customName;
  }
}
