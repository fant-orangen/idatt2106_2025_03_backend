package stud.ntnu.backend.model.group;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import stud.ntnu.backend.model.household.Household;
import stud.ntnu.backend.model.inventory.ProductBatch;

@Entity
@Table(name = "group_inventory_contributions")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
  private LocalDateTime expirationDate;

  @Column(name = "contributed_at", nullable = false)
  private LocalDateTime contributedAt;

  public GroupInventoryContribution(Group group, Household household, ProductBatch product) {
    this.group = group;
    this.household = household;
    this.product = product;
    this.contributedAt = LocalDateTime.now();
  }

  public GroupInventoryContribution(Group group, Household household, String customName) {
    this.group = group;
    this.household = household;
    this.customName = customName;
  }
}
