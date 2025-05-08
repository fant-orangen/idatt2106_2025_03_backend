package stud.ntnu.backend.dto.quiz;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for quiz answers.
 * <p>
 * This DTO represents a quiz answer entity, containing information about the answer's
 * content, correctness, and its relationships to quizzes and questions.
 */
@Getter
@Setter
public class QuizAnswerDto {
    /**
     * The unique identifier of the answer.
     */
    private Long id;

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

    /**
     * Default constructor.
     */
    public QuizAnswerDto() {}

    /**
     * Constructor with all fields.
     *
     * @param id The unique identifier of the answer
     * @param quizId The unique identifier of the quiz
     * @param questionId The unique identifier of the question
     * @param answerBody The content of the answer
     * @param isCorrect Whether the answer is correct
     * @param createdAt The creation timestamp (unused in this DTO)
     */
    public QuizAnswerDto(Long id, Long quizId, Long questionId, String answerBody, Boolean isCorrect, LocalDateTime createdAt) {
        this.id = id;
        this.quizId = quizId;
        this.questionId = questionId;
        this.answerBody = answerBody;
        this.isCorrect = isCorrect;
    }
}