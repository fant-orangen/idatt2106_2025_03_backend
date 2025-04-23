package stud.ntnu.backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "crisis_events")
@Getter
@Setter
@NoArgsConstructor
public class CrisisEvent {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "severity", nullable = false)
  private Severity severity = Severity.GREEN;

  @Column(name = "epicenter_latitude", nullable = false, precision = 10, scale = 7)
  private BigDecimal epicenterLatitude;

  @Column(name = "epicenter_longitude", nullable = false, precision = 10, scale = 7)
  private BigDecimal epicenterLongitude;

  @Column(name = "start_time", nullable = false)
  private LocalDateTime startTime;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @ManyToOne
  @JoinColumn(name = "created_by_user_id", nullable = false)
  private User createdByUser;

  @Column(name = "active", nullable = false)
  private Boolean active = true;

  // Set updatedAt before update
  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

  public CrisisEvent(String name, BigDecimal epicenterLatitude, BigDecimal epicenterLongitude,
      LocalDateTime startTime, User createdByUser) {
    this.name = name;
    this.epicenterLatitude = epicenterLatitude;
    this.epicenterLongitude = epicenterLongitude;
    this.startTime = startTime;
    this.createdByUser = createdByUser;
    this.updatedAt = LocalDateTime.now();
  }

  // Enum for severity
  public enum Severity {
    GREEN, YELLOW, RED
  }
}
