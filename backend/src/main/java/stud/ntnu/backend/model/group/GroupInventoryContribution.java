package stud.ntnu.backend.model.group;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stud.ntnu.backend.model.household.Household;
import stud.ntnu.backend.model.inventory.Product;
import stud.ntnu.backend.model.inventory.ProductBatch;

@Entity
@Table(name = "group_inventory_contributions")
@Getter
@Setter
@NoArgsConstructor
public class GroupInventoryContribution {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "group_id", nullable = false)
  private Group group;

  @ManyToOne
  @JoinColumn(name = "household_id", nullable = false)
  private Household household;

  @ManyToOne
  @JoinColumn(name = "product_id")
  private ProductBatch product;

  @Column(name = "custom_name")
  private String customName;

  @Column(name = "expiration_date")
  private LocalDate expirationDate;

  @Column(name = "contributed_at", nullable = false, updatable = false)
  private LocalDateTime contributedAt;

  // Set contributedAt before persist
  @PrePersist
  protected void onCreate() {
    contributedAt = LocalDateTime.now();
  }

  public GroupInventoryContribution(Group group, Household household, ProductBatch product) {
    this.group = group;
    this.household = household;
    this.product = product;
  }

  public GroupInventoryContribution(Group group, Household household, String customName) {
    this.group = group;
    this.household = household;
    this.customName = customName;
  }
}
