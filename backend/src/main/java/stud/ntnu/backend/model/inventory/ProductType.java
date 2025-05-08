package stud.ntnu.backend.model.inventory;

import jakarta.persistence.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import stud.ntnu.backend.model.household.Household;

@Entity
@Table(name = "product_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductType {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "household_id", nullable = false)
  private Household household;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String unit;

  @Column(name = "calories_per_unit")
  private Double caloriesPerUnit;

  @Column(nullable = false)
  private String category;

  @OneToMany(mappedBy = "productType")
  private List<ProductBatch> productBatches;

  public ProductType(Household household, String name, String unit, Double caloriesPerUnit, String category) {
    this.household = household;
    this.name = name;
    this.unit = unit;
    this.caloriesPerUnit = caloriesPerUnit;
    this.category = category;
  }
}
