package stud.ntnu.backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "crisis_events")
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

  // Constructors
  public CrisisEvent() {
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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Severity getSeverity() {
    return severity;
  }

  public void setSeverity(Severity severity) {
    this.severity = severity;
  }

  public BigDecimal getEpicenterLatitude() {
    return epicenterLatitude;
  }

  public void setEpicenterLatitude(BigDecimal epicenterLatitude) {
    this.epicenterLatitude = epicenterLatitude;
  }

  public BigDecimal getEpicenterLongitude() {
    return epicenterLongitude;
  }

  public void setEpicenterLongitude(BigDecimal epicenterLongitude) {
    this.epicenterLongitude = epicenterLongitude;
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(LocalDateTime startTime) {
    this.startTime = startTime;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public User getCreatedByUser() {
    return createdByUser;
  }

  public void setCreatedByUser(User createdByUser) {
    this.createdByUser = createdByUser;
  }

  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }
}