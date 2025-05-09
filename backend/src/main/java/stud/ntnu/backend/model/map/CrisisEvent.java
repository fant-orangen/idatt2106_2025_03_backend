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

/**
 * Represents a crisis event in the system. This entity tracks information about crisis events
 * including their location, severity, timing, and associated metadata.
 */
@Entity
@Table(name = "crisis_events")
@Getter
@Setter
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class CrisisEvent {

  /**
   * Unique identifier for the crisis event.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /**
   * The name of the crisis event.
   */
  @Column(name = "name", nullable = false)
  private String name;

  /**
   * Detailed description of the crisis event. Stored as TEXT in the database.
   */
  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  /**
   * The severity level of the crisis event. Default value is green.
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "severity", nullable = false)
  private Severity severity = Severity.green;

  /**
   * The latitude coordinate of the crisis event's epicenter. Stored with 7 decimal places
   * precision.
   */
  @Column(name = "epicenter_latitude", nullable = false, precision = 10, scale = 7)
  private BigDecimal epicenterLatitude;

  /**
   * The longitude coordinate of the crisis event's epicenter. Stored with 7 decimal places
   * precision.
   */
  @Column(name = "epicenter_longitude", nullable = false, precision = 10, scale = 7)
  private BigDecimal epicenterLongitude;

  /**
   * The radius of the affected area in the specified unit. Stored with 2 decimal places precision.
   */
  @Column(name = "radius", precision = 10, scale = 2)
  private BigDecimal radius;

  /**
   * The time when the crisis event started.
   */
  @Column(name = "start_time", nullable = false)
  private LocalDateTime startTime;

  /**
   * The time when the crisis event was last updated.
   */
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  /**
   * The user who created this crisis event.
   */
  @ManyToOne
  @JoinColumn(name = "created_by_user_id", nullable = false)
  @JsonIdentityReference(alwaysAsId = true)
  private User createdByUser;

  /**
   * Indicates whether the crisis event is currently active. Default value is true.
   */
  @Column(name = "active", nullable = false)
  private Boolean active = true;

  /**
   * The scenario theme associated with this crisis event, if any.
   */
  @ManyToOne
  @JoinColumn(name = "scenario_theme_id")
  private ScenarioTheme scenarioTheme;

  /**
   * Updates the last modified timestamp before any update operation.
   */
  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

  /**
   * Creates a new crisis event with the specified parameters.
   *
   * @param name               The name of the crisis event
   * @param epicenterLatitude  The latitude coordinate of the epicenter
   * @param epicenterLongitude The longitude coordinate of the epicenter
   * @param radius             The radius of the affected area
   * @param startTime          The time when the crisis event started
   * @param createdByUser      The user who created this crisis event
   */
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

  /**
   * Represents the severity levels of a crisis event.
   */
  public enum Severity {
    green, yellow, red
  }
}
