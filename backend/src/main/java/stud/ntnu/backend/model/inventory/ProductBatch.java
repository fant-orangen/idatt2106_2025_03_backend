package stud.ntnu.backend.model.inventory;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_batch")
@Getter
@Setter
@NoArgsConstructor
public class ProductBatch {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "product_type_id", nullable = false)
  private ProductType productType;

  @Column(name = "date_added", nullable = false)
  private LocalDateTime dateAdded;

  @Column(name = "expiration_time")
  private LocalDateTime expirationTime;

  @Column(name = "number", nullable = false)
  private Integer number;

  public ProductBatch(ProductType productType, LocalDateTime dateAdded, LocalDateTime expirationTime, Integer number) {
    this.productType = productType;
    this.dateAdded = dateAdded;
    this.expirationTime = expirationTime;
    this.number = number;
  }
}