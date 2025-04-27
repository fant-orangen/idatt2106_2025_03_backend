package stud.ntnu.backend.model.household;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stud.ntnu.backend.model.user.User;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "households")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Household {

  // Getters and Setters
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "name")
  private String name;

  @Column(name = "address", columnDefinition = "TEXT", nullable = false)
  private String address;

  @Column(name = "population_count", nullable = false)
  private Integer populationCount = 1;

  @Column(name = "latitude", precision = 10, scale = 7)
  private BigDecimal latitude;

  @Column(name = "longitude", precision = 10, scale = 7)
  private BigDecimal longitude;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @OneToMany(mappedBy = "household")
  @JsonManagedReference
  private List<User> users;

  // Set createdAt before persist
  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }

  public Household(String name, String address, Integer populationCount) {
    this.name = name;
    this.address = address;
    this.populationCount = populationCount;
  }

  public Household(String name, Integer populationCount) {
    this.name = name;
    this.populationCount = populationCount;
  }

}
