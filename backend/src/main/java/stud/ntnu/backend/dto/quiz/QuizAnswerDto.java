package stud.ntnu.backend.dto.quiz;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object for QuizAnswer.
 */
@Getter
@Setter
public class QuizAnswerDto {
    private Long id;
    private Long quizId;
    private Long questionId;
    private String answerBody;
    private Boolean isCorrect;

    // Default constructor
    public QuizAnswerDto() {}

    // Constructor with all fields
    public QuizAnswerDto(Long id, Long quizId, Long questionId, String answerBody, Boolean isCorrect, LocalDateTime createdAt) {
        this.id = id;
        this.quizId = quizId;
        this.questionId = questionId;
        this.answerBody = answerBody;
        this.isCorrect = isCorrect;
    }
}