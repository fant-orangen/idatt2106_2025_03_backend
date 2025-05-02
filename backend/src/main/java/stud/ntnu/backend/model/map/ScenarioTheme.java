package stud.ntnu.backend.model.map;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import stud.ntnu.backend.model.user.User;

@Setter
@Getter
@Entity
@Table(name = "scenario_themes")
public class ScenarioTheme {

  // Getters and Setters
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "name", nullable = false, unique = true)
  private String name;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "instructions", columnDefinition = "TEXT")
  private String instructions;

  @ManyToOne
  @JoinColumn(name = "created_by_user_id", nullable = false)
  private User createdByUser;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Column(name = "status", nullable = false)
  private String status = "active";

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

  // Constructors
  public ScenarioTheme() {
  }

  public ScenarioTheme(String name, User createdByUser) {
    this.name = name;
    this.createdByUser = createdByUser;
  }

}