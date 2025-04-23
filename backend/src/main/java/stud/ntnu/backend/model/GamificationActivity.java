package stud.ntnu.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "gamification_activities")
public class GamificationActivity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "name", nullable = false, unique = true)
  private String name;

  @Column(name = "category")
  private String category;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @ManyToOne
  @JoinColumn(name = "created_by_user_id", nullable = false)
  private User createdByUser;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "activity")
  private List<UserGamificationActivity> userActivities;

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
  public GamificationActivity() {
  }

  public GamificationActivity(String name, User createdByUser) {
    this.name = name;
    this.createdByUser = createdByUser;
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

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public User getCreatedByUser() {
    return createdByUser;
  }

  public void setCreatedByUser(User createdByUser) {
    this.createdByUser = createdByUser;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public List<UserGamificationActivity> getUserActivities() {
    return userActivities;
  }

  public void setUserActivities(List<UserGamificationActivity> userActivities) {
    this.userActivities = userActivities;
  }
}
