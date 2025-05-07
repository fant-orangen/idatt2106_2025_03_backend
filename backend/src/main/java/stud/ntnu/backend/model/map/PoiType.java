package stud.ntnu.backend.model.map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@Table(name = "poi_types")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class PoiType {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "name", nullable = false, unique = true, length = 100)
  private String name;

  @JsonIgnore
  @OneToMany(mappedBy = "poiType")
  private List<PointOfInterest> pointsOfInterest;

  public PoiType(String name) {
    this.name = name;
  }
}
