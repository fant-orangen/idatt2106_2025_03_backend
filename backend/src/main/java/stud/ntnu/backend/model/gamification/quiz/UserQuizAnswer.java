package stud.ntnu.backend.model.gamification.quiz;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * Entity class representing a user's answer to a quiz question. This class maps to the
 * 'user_quiz_answers' table in the database and tracks which answers users select for specific
 * questions in a quiz attempt.
 */
@Setter
@Getter
@Entity
@Table(name = "user_quiz_answers")
public class UserQuizAnswer {

  /**
   * Unique identifier for the user's quiz answer.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * ID of the quiz attempt this answer belongs to. Links to a specific attempt by a user to
   * complete a quiz.
   */
  @Column(name = "user_quiz_attempt_id", nullable = false)
  private Long userQuizAttemptId;

  /**
   * ID of the quiz this answer is associated with.
   */
  @Column(name = "quiz_id", nullable = false)
  private Long quizId;

  /**
   * ID of the specific question this answer corresponds to.
   */
  @Column(name = "question_id", nullable = false)
  private Long questionId;

  /**
   * ID of the answer option selected by the user. References the specific answer choice from the
   * available options.
   */
  @Column(name = "answer_id", nullable = false)
  private Long answerId;

  /**
   * Default constructor required by JPA.
   */
  public UserQuizAnswer() {
  }
}