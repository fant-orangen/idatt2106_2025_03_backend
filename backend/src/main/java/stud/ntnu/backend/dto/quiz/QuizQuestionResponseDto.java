package stud.ntnu.backend.dto.quiz;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing a quiz question response. This class is used to transfer
 * quiz question data between layers of the application.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestionResponseDto {

  /**
   * The unique identifier of the quiz question.
   */
  private Long id;

  /**
   * The text content of the quiz question.
   */
  private String questionBody;
}