package stud.ntnu.backend.dto.quiz;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for creating a new quiz question.
 * <p>
 * This DTO contains all the necessary information required to create a new quiz question,
 * including references to the quiz, the question content, and its position in the quiz.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateQuizQuestionDto {

    /**
     * The unique identifier of the quiz this question belongs to.
     */
    private Long quizId;

    /**
     * The content or text of the question.
     */
    private String questionBody;

    /**
     * The position of the question within the quiz.
     * This field is optional and can be null.
     */
    private Integer position;
}