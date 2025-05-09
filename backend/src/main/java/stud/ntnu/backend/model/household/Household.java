package stud.ntnu.backend.model.household;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import stud.ntnu.backend.model.user.User;

/**
 * Represents a household entity in the system. A household is a collection of users living together
 * at a specific location. Each household has a name, address, population count, and optional
 * geographical coordinates.
 */
@Entity
@Table(name = "households")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Household {

  /**
   * Unique identifier for the household.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /**
   * Name of the household.
   */
  @Column(name = "name")
  private String name;

  /**
   * Physical address of the household. Cannot be null and is stored as TEXT to accommodate long
   * addresses.
   */
  @Column(name = "address", columnDefinition = "TEXT", nullable = false)
  private String address;

  /**
   * Number of people living in the household. Defaults to 1 if not specified.
   */
  @Column(name = "population_count", nullable = false)
  private Integer populationCount = 1;

  /**
   * Latitude coordinate of the household's location. Stored with 7 decimal places precision.
   */
  @Column(name = "latitude", precision = 10, scale = 7)
  private BigDecimal latitude;

  /**
   * Longitude coordinate of the household's location. Stored with 7 decimal places precision.
   */
  @Column(name = "longitude", precision = 10, scale = 7)
  private BigDecimal longitude;

  /**
   * Timestamp when the household was created. Cannot be null and cannot be updated after creation.
   */
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /**
   * List of users associated with this household. Initialized as an empty ArrayList.
   */
  @OneToMany(mappedBy = "household")
  private List<User> users = new ArrayList<>();

  /**
   * Sets the creation timestamp to the current time before persisting the entity.
   */
  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }

  /**
   * Creates a new household with name, address, and population count.
   *
   * @param name            The name of the household
   * @param address         The physical address of the household
   * @param populationCount The number of people living in the household
   */
  public Household(String name, String address, Integer populationCount) {
    this.name = name;
    this.address = address;
    this.populationCount = populationCount;
  }

  /**
   * Creates a new household with name and population count.
   *
   * @param name            The name of the household
   * @param populationCount The number of people living in the household
   */
  public Household(String name, Integer populationCount) {
    this.name = name;
    this.populationCount = populationCount;
  }
}
