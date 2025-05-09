package stud.ntnu.backend.model.gamification.quiz;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

/**
 * Entity class representing a question in a quiz. This class maps to the 'quiz_questions' table in
 * the database.
 */
@Setter
@Getter
@Entity
@Table(name = "quiz_questions")
public class QuizQuestion {

  /**
   * Unique identifier for the quiz question.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * ID of the quiz this question belongs to.
   */
  @Column(name = "quiz_id", nullable = false)
  private Long quizId;

  /**
   * The actual question text/content.
   */
  @Column(name = "question_body", nullable = false)
  private String questionBody;

  /**
   * Timestamp when the question was created. This field is automatically set and cannot be
   * updated.
   */
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /**
   * Position of the question within the quiz. Used to maintain the order of questions.
   */
  @Column(name = "position")
  private Integer position;

  /**
   * Automatically sets the creation timestamp when a new question is persisted.
   */
  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }

  /**
   * Default constructor required by JPA.
   */
  public QuizQuestion() {
  }
}