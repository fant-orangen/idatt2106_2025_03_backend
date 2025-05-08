package stud.ntnu.backend.dto.quiz;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for creating a new user quiz answer.
 * <p>
 * This DTO contains all the necessary information required to create a new user quiz answer,
 * including references to the quiz attempt, quiz, question, and selected answer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserQuizAnswerDto {

    /**
     * The unique identifier of the user's quiz attempt.
     */
    private Long userQuizAttemptId;

    /**
     * The unique identifier of the quiz.
     */
    private Long quizId;

    /**
     * The unique identifier of the question being answered.
     */
    private Long questionId;

    /**
     * The unique identifier of the selected answer.
     */
    private Long answerId;
}