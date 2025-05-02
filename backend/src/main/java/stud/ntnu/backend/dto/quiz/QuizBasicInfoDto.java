package stud.ntnu.backend.dto.quiz;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizBasicInfoDto {
    private Long quizId;
    private String name;
    private String status;
    private Long questionCount;
} 