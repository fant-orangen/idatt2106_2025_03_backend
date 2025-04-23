package stud.ntnu.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_gamification_activities")
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

  // Constructors
  public UserGamificationActivity() {
  }

  public UserGamificationActivity(User user, GamificationActivity activity) {
    this.user = user;
    this.activity = activity;
  }

  // Enum for status
  public enum Status {
    PENDING, COMPLETED, FAILED
  }

  // Getters and Setters
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public GamificationActivity getActivity() {
    return activity;
  }

  public void setActivity(GamificationActivity activity) {
    this.activity = activity;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public Integer getScore() {
    return score;
  }

  public void setScore(Integer score) {
    this.score = score;
  }

  public LocalDateTime getCompletedAt() {
    return completedAt;
  }

  public void setCompletedAt(LocalDateTime completedAt) {
    this.completedAt = completedAt;
  }
}