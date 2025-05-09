package stud.ntnu.backend.model.user;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity class representing a safety confirmation record in the system. This class stores
 * information about a user's safety status at a specific time.
 */
@Entity
@Table(name = "safety_confirmations")
@Getter
@Setter
@NoArgsConstructor
public class SafetyConfirmation {

  /**
   * Unique identifier for the safety confirmation record.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /**
   * The user associated with this safety confirmation.
   */
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  /**
   * Indicates whether the user was safe at the time of confirmation.
   */
  @Column(name = "is_safe", nullable = false)
  private Boolean isSafe;

  /**
   * The timestamp when the safety status was confirmed.
   */
  @Column(name = "safe_at", nullable = false)
  private LocalDateTime safeAt;

  /**
   * Constructs a new SafetyConfirmation with the specified user, safety status, and timestamp.
   *
   * @param user   The user associated with this safety confirmation
   * @param isSafe The safety status of the user
   * @param safeAt The timestamp of the safety confirmation
   */
  public SafetyConfirmation(User user, Boolean isSafe, LocalDateTime safeAt) {
    this.user = user;
    this.isSafe = isSafe;
    this.safeAt = safeAt;
  }
}