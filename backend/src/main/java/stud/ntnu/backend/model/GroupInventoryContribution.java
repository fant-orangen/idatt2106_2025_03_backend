package stud.ntnu.backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "group_inventory_contributions")
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

  // Constructors
  public GroupInventoryContribution() {
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

  // Getters and Setters
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Group getGroup() {
    return group;
  }

  public void setGroup(Group group) {
    this.group = group;
  }

  public Household getHousehold() {
    return household;
  }

  public void setHousehold(Household household) {
    this.household = household;
  }

  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
    this.product = product;
  }

  public String getCustomName() {
    return customName;
  }

  public void setCustomName(String customName) {
    this.customName = customName;
  }

  public BigDecimal getQuantity() {
    return quantity;
  }

  public void setQuantity(BigDecimal quantity) {
    this.quantity = quantity;
  }

  public LocalDate getExpirationDate() {
    return expirationDate;
  }

  public void setExpirationDate(LocalDate expirationDate) {
    this.expirationDate = expirationDate;
  }

  public LocalDateTime getContributedAt() {
    return contributedAt;
  }

  public void setContributedAt(LocalDateTime contributedAt) {
    this.contributedAt = contributedAt;
  }
}