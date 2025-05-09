package stud.ntnu.backend.model.map;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a type of point of interest in the system. This entity defines categories or
 * classifications for points of interest, such as restaurants, museums, parks, etc.
 */
@Entity
@Table(name = "poi_types")
@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class PoiType {

  /**
   * Unique identifier for the point of interest type.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /**
   * The name of the point of interest type. Must be unique and cannot exceed 100 characters.
   */
  @Column(name = "name", nullable = false, unique = true, length = 100)
  private String name;

  /**
   * List of points of interest associated with this type. This relationship is ignored during JSON
   * serialization to prevent circular references.
   */
  @JsonIgnore
  @OneToMany(mappedBy = "poiType")
  private List<PointOfInterest> pointsOfInterest;

  /**
   * Constructs a new point of interest type with the specified name.
   *
   * @param name The name of the point of interest type
   */
  public PoiType(String name) {
    this.name = name;
  }
}
