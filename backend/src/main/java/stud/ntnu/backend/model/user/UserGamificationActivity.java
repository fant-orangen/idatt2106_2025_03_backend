package stud.ntnu.backend.model.user;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import stud.ntnu.backend.model.gamification.GamificationActivity;

/**
 * Entity class representing a user's gamification activity in the system. This class tracks a
 * user's progress and completion status for specific gamification activities, including scores and
 * completion timestamps.
 */
@Entity
@Table(name = "user_gamification_activities")
@Getter
@Setter
@NoArgsConstructor
public class UserGamificationActivity {

  /**
   * Unique identifier for the user gamification activity.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /**
   * The user associated with this gamification activity.
   */
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  /**
   * The gamification activity being tracked.
   */
  @ManyToOne
  @JoinColumn(name = "activity_id", nullable = false)
  private GamificationActivity activity;

  /**
   * The current status of the activity. Defaults to PENDING when created.
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private Status status = Status.PENDING;

  /**
   * The score achieved for this activity, if applicable.
   */
  @Column(name = "score")
  private Integer score;

  /**
   * The timestamp when the activity was completed.
   */
  @Column(name = "completed_at")
  private LocalDateTime completedAt;

  /**
   * Constructs a new user gamification activity with the specified user and activity.
   *
   * @param user     The user associated with this activity
   * @param activity The gamification activity to track
   */
  public UserGamificationActivity(User user, GamificationActivity activity) {
    this.user = user;
    this.activity = activity;
  }

  /**
   * Enum representing the possible statuses of a gamification activity.
   */
  public enum Status {
    /**
     * Activity is pending completion
     */
    PENDING,
    /**
     * Activity has been successfully completed
     */
    COMPLETED,
    /**
     * Activity was attempted but failed
     */
    FAILED
  }
}
