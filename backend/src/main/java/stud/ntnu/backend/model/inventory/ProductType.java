package stud.ntnu.backend.model.inventory;

import jakarta.persistence.*;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stud.ntnu.backend.model.household.Household;

@Entity
@Table(name = "product_types")
@Getter
@Setter
@NoArgsConstructor
public class ProductType {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "household_id", nullable = false)
  private Household household;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "unit", nullable = false)
  private String unit;

  @Column(name = "calories_per_unit")
  private Double caloriesPerUnit;

  @Column(name = "is_water", nullable = false)
  private Boolean isWater;

  @OneToMany(mappedBy = "productType")
  private List<ProductBatch> productBatches;

  public ProductType(Household household, String name, String unit, Double caloriesPerUnit, Boolean isWater) {
    this.household = household;
    this.name = name;
    this.unit = unit;
    this.caloriesPerUnit = caloriesPerUnit;
    this.isWater = isWater;
  }
}
