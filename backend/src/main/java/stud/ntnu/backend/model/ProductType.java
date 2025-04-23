package stud.ntnu.backend.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "product_types")
public class ProductType {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "name", nullable = false, unique = true)
  private String name;

  @OneToMany(mappedBy = "productType")
  private List<Product> products;

  // Constructors
  public ProductType() {
  }

  public ProductType(String name) {
    this.name = name;
  }

  // Getters and Setters
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Product> getProducts() {
    return products;
  }

  public void setProducts(List<Product> products) {
    this.products = products;
  }
}
