package stud.ntnu.backend.dto.quiz;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for creating a new quiz answer.
 * <p>
 * This DTO contains all the necessary information required to create a new quiz answer,
 * including references to the quiz and question, the answer content, and its correctness.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateQuizAnswerDto {

    /**
     * The unique identifier of the quiz this answer belongs to.
     */
    private Long quizId;

    /**
     * The unique identifier of the question this answer is for.
     */
    private Long questionId;

    /**
     * The content or text of the answer.
     */
    private String answerBody;

    /**
     * Indicates whether this answer is correct or not.
     */
    private Boolean isCorrect;
}