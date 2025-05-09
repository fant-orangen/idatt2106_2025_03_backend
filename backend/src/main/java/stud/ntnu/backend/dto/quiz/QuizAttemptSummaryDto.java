package stud.ntnu.backend.dto.quiz;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing a summary of a quiz attempt. This class contains basic
 * information about a completed quiz attempt.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttemptSummaryDto {

  /**
   * The unique identifier of the quiz attempt.
   */
  private Long id;

  /**
   * The date and time when the quiz attempt was completed.
   */
  private LocalDateTime completedAt;
}