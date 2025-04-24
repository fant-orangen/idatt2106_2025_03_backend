package stud.ntnu.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "households")
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

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @OneToMany(mappedBy = "household")
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
