package stud.ntnu.backend.model.gamification;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.model.user.UserGamificationActivity;

@Entity
@Table(name = "gamification_activities")
@Getter
@Setter
@NoArgsConstructor
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

  public GamificationActivity(String name, User createdByUser) {
    this.name = name;
    this.createdByUser = createdByUser;
  }
}
