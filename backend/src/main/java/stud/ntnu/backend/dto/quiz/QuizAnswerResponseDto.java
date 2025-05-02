package stud.ntnu.backend.dto.quiz;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizAnswerResponseDto {
    private Long id;
    private Long quizId;
    private Long questionId;
    private String answerBody;
} 