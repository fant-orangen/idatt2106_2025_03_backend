package stud.ntnu.backend.dto.quiz;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateQuizQuestionDto {
    private Long quizId;
    private String questionBody;
    private Integer position; // nullable
} 