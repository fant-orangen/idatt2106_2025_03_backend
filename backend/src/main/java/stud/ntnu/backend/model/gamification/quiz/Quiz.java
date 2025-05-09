package stud.ntnu.backend.model.gamification.quiz;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * Entity class representing a quiz in the gamification system. Quizzes are used to test users'
 * knowledge and provide educational content. Each quiz has a name, description, status, and is
 * associated with a creator.
 */
@Setter
@Getter
@Entity
@Table(name = "quizzes")
public class Quiz {

  /**
   * Unique identifier for the quiz.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The name of the quiz. This field cannot be null.
   */
  @Column(name = "name", nullable = false)
  private String name;

  /**
   * A detailed description of the quiz. This field can be null.
   */
  @Column(name = "description")
  private String description;

  /**
   * The current status of the quiz. Default value is "active". This field cannot be null.
   */
  @Column(name = "status", nullable = false)
  private String status = "active";

  /**
   * The ID of the user who created this quiz. This field cannot be null.
   */
  @Column(name = "created_by_user_id", nullable = false)
  private Long createdByUserId;

  /**
   * The timestamp when the quiz was created. This field cannot be null and cannot be updated after
   * creation.
   */
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /**
   * Sets the creation timestamp to the current time before persisting the entity.
   */
  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }

  /**
   * Default constructor required by JPA.
   */
  public Quiz() {
  }
}