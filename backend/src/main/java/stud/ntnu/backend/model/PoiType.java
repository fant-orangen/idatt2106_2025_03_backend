package stud.ntnu.backend.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "poi_types")
public class PoiType {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "name", nullable = false, unique = true, length = 100)
  private String name;

  @OneToMany(mappedBy = "poiType")
  private List<PointOfInterest> pointsOfInterest;

  // Constructors
  public PoiType() {
  }

  public PoiType(String name) {
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

  public List<PointOfInterest> getPointsOfInterest() {
    return pointsOfInterest;
  }

  public void setPointsOfInterest(List<PointOfInterest> pointsOfInterest) {
    this.pointsOfInterest = pointsOfInterest;
  }
}
