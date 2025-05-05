package stud.ntnu.backend.model.map;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stud.ntnu.backend.model.user.User;

@Entity
@Table(name = "meeting_places")
@Getter
@Setter
@NoArgsConstructor
public class MeetingPlace {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "latitude", nullable = false, precision = 10, scale = 7)
  private BigDecimal latitude;

  @Column(name = "longitude", nullable = false, precision = 10, scale = 7)
  private BigDecimal longitude;

  @Column(name = "address", columnDefinition = "TEXT")
  private String address;

  @Column(name = "status", nullable = false)
  private String status = "active";

  @ManyToOne
  @JoinColumn(name = "created_by_user_id", nullable = false)
  private User createdByUser;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  // Set createdAt before persist
  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }

  public MeetingPlace(String name, BigDecimal latitude, BigDecimal longitude, User createdByUser) {
    this.name = name;
    this.latitude = latitude;
    this.longitude = longitude;
    this.createdByUser = createdByUser;
  }
}
