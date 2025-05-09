package stud.ntnu.backend.dto.quiz;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing a quiz answer response. This class is used to transfer
 * quiz answer data between layers of the application.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizAnswerResponseDto {

  /**
   * Unique identifier for the quiz answer.
   */
  private Long id;

  /**
   * Identifier of the quiz this answer belongs to.
   */
  private Long quizId;

  /**
   * Identifier of the question this answer corresponds to.
   */
  private Long questionId;

  /**
   * The actual answer content provided by the user.
   */
  private String answerBody;
}