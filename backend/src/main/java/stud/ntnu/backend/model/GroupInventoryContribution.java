package stud.ntnu.backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
  private Product product;

  @Column(name = "custom_name")
  private String customName;

  @Column(name = "quantity", nullable = false, precision = 10, scale = 2)
  private BigDecimal quantity;

  @Column(name = "expiration_date")
  private LocalDate expirationDate;

  @Column(name = "contributed_at", nullable = false, updatable = false)
  private LocalDateTime contributedAt;

  // Set contributedAt before persist
  @PrePersist
  protected void onCreate() {
    contributedAt = LocalDateTime.now();
  }

  public GroupInventoryContribution(Group group, Household household, Product product,
      BigDecimal quantity) {
    this.group = group;
    this.household = household;
    this.product = product;
    this.quantity = quantity;
  }

  public GroupInventoryContribution(Group group, Household household, String customName,
      BigDecimal quantity) {
    this.group = group;
    this.household = household;
    this.customName = customName;
    this.quantity = quantity;
  }
}
