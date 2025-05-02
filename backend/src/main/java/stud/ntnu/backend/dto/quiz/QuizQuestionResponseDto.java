package stud.ntnu.backend.dto.quiz;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestionResponseDto {
    private Long id;
    private String questionBody;
} 