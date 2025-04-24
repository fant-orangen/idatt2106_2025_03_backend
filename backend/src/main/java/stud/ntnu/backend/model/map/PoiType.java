package stud.ntnu.backend.model.map;

import jakarta.persistence.*;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "poi_types")
@Getter
@Setter
@NoArgsConstructor
public class PoiType {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "name", nullable = false, unique = true, length = 100)
  private String name;

  @OneToMany(mappedBy = "poiType")
  private List<PointOfInterest> pointsOfInterest;

  public PoiType(String name) {
    this.name = name;
  }
}
