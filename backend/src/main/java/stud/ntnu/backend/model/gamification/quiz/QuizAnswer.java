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
 * Entity class representing a quiz answer in the system. This class maps to the 'quiz_answers'
 * table in the database.
 */
@Setter
@Getter
@Entity
@Table(name = "quiz_answers")
public class QuizAnswer {

  /**
   * Unique identifier for the quiz answer.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * ID of the quiz this answer belongs to.
   */
  @Column(name = "quiz_id", nullable = false)
  private Long quizId;

  /**
   * ID of the question this answer corresponds to.
   */
  @Column(name = "question_id", nullable = false)
  private Long questionId;

  /**
   * The actual answer content provided by the user.
   */
  @Column(name = "answer_body", nullable = false)
  private String answerBody;

  /**
   * Flag indicating whether the answer is correct or not.
   */
  @Column(name = "is_correct", nullable = false)
  private Boolean isCorrect;

  /**
   * Timestamp when the answer was created. This field is automatically set and cannot be updated.
   */
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /**
   * Sets the creation timestamp before persisting the entity.
   */
  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }

  /**
   * Default constructor required by JPA.
   */
  public QuizAnswer() {
  }
}