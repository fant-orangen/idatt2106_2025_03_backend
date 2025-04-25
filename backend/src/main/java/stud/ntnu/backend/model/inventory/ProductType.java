package stud.ntnu.backend.model.inventory;

import jakarta.persistence.*;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_types")
@Getter
@Setter
@NoArgsConstructor
public class ProductType {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "name", nullable = false, unique = true)
  private String name;

  @Column(name = "unit", nullable = false)
  private String unit;

  @Column(name = "calories_per_unit")
  private Double caloriesPerUnit;

  @Column(name = "is_water", nullable = false)
  private Boolean isWater;

  @OneToMany(mappedBy = "productType")
  private List<ProductBatch> productBatches;

  public ProductType(String name, String unit, Double caloriesPerUnit, Boolean isWater) {
    this.name = name;
    this.unit = unit;
    this.caloriesPerUnit = caloriesPerUnit;
    this.isWater = isWater;
  }
}
