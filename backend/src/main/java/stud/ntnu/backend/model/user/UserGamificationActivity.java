package stud.ntnu.backend.model.user;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stud.ntnu.backend.model.information.GamificationActivity;

@Entity
@Table(name = "user_gamification_activities")
@Getter
@Setter
@NoArgsConstructor
public class UserGamificationActivity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "activity_id", nullable = false)
  private GamificationActivity activity;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private Status status = Status.PENDING;

  @Column(name = "score")
  private Integer score;

  @Column(name = "completed_at")
  private LocalDateTime completedAt;

  public UserGamificationActivity(User user, GamificationActivity activity) {
    this.user = user;
    this.activity = activity;
  }

  // Enum for status
  public enum Status {
    PENDING, COMPLETED, FAILED
  }
}
