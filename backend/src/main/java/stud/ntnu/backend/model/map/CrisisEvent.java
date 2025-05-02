package stud.ntnu.backend.model.map;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stud.ntnu.backend.model.user.User;

@Entity
@Table(name = "crisis_events")
@Getter
@Setter
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
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
  private Severity severity = Severity.green;

  @Column(name = "epicenter_latitude", nullable = false, precision = 10, scale = 7)
  private BigDecimal epicenterLatitude;

  @Column(name = "epicenter_longitude", nullable = false, precision = 10, scale = 7)
  private BigDecimal epicenterLongitude;

  @Column(name = "radius", precision = 10, scale = 2)
  private BigDecimal radius;

  @Column(name = "start_time", nullable = false)
  private LocalDateTime startTime;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @ManyToOne
  @JoinColumn(name = "created_by_user_id", nullable = false)
  @JsonIdentityReference(alwaysAsId = true)
  private User createdByUser;

  @Column(name = "active", nullable = false)
  private Boolean active = true;

  @ManyToOne
  @JoinColumn(name = "scenario_theme_id")
  private ScenarioTheme scenarioTheme;

  // Set updatedAt before update
  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

  public CrisisEvent(String name, BigDecimal epicenterLatitude, BigDecimal epicenterLongitude,
      BigDecimal radius, LocalDateTime startTime, User createdByUser) {
    this.name = name;
    this.epicenterLatitude = epicenterLatitude;
    this.epicenterLongitude = epicenterLongitude;
    this.radius = radius;
    this.startTime = startTime;
    this.createdByUser = createdByUser;
    this.updatedAt = LocalDateTime.now();
  }

  // Enum for severity
  public enum Severity {
    green, yellow, red
  }
}
