package stud.ntnu.backend.model.inventory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import stud.ntnu.backend.model.household.Household;

/**
 * Represents a type of product in the inventory system. Each product type belongs to a household
 * and can have multiple product batches.
 */
@Entity
@Table(name = "product_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductType {

  /**
   * Unique identifier for the product type.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /**
   * The household this product type belongs to.
   */
  @ManyToOne
  @JoinColumn(name = "household_id", nullable = false)
  private Household household;

  /**
   * The name of the product type.
   */
  @Column(nullable = false)
  private String name;

  /**
   * The unit of measurement for this product type (e.g., kg, liters, pieces).
   */
  @Column(nullable = false)
  private String unit;

  /**
   * The number of calories per unit of this product type.
   */
  @Column(name = "calories_per_unit")
  private Double caloriesPerUnit;

  /**
   * The category this product type belongs to (e.g., dairy, meat, vegetables).
   */
  @Column(nullable = false)
  private String category;

  /**
   * List of product batches associated with this product type.
   */
  @OneToMany(mappedBy = "productType")
  private List<ProductBatch> productBatches;

  /**
   * Constructs a new ProductType with the specified parameters.
   *
   * @param household       The household this product type belongs to
   * @param name            The name of the product type
   * @param unit            The unit of measurement
   * @param caloriesPerUnit The number of calories per unit
   * @param category        The category of the product type
   */
  public ProductType(Household household, String name, String unit, Double caloriesPerUnit,
      String category) {
    this.household = household;
    this.name = name;
    this.unit = unit;
    this.caloriesPerUnit = caloriesPerUnit;
    this.category = category;
  }
}
