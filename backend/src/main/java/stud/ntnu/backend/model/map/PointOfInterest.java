package stud.ntnu.backend.model.map;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stud.ntnu.backend.model.user.User;

@Entity
@Table(name = "points_of_interest")
@Getter
@Setter
@NoArgsConstructor
public class PointOfInterest {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "poi_type_id", nullable = false)
  private PoiType poiType;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "latitude", nullable = false, precision = 10, scale = 7)
  private BigDecimal latitude;

  @Column(name = "longitude", nullable = false, precision = 10, scale = 7)
  private BigDecimal longitude;

  @Column(name = "address", columnDefinition = "TEXT")
  private String address;

  @Column(name = "open_from", columnDefinition = "TEXT")
  private String openFrom;

    @Column(name = "open_to", columnDefinition = "TEXT")
    private String openTo;

  @Column(name = "contact_info", columnDefinition = "TEXT")
  private String contactInfo;

  @ManyToOne
  @JoinColumn(name = "created_by_user_id", nullable = false)
  private User createdByUser;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  // Set createdAt and updatedAt before persist
  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
  }

  // Set updatedAt before update
  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

  public PointOfInterest(PoiType poiType, String name, BigDecimal latitude, BigDecimal longitude,
      User createdByUser) {
    this.poiType = poiType;
    this.name = name;
    this.latitude = latitude;
    this.longitude = longitude;
    this.createdByUser = createdByUser;
  }
}
