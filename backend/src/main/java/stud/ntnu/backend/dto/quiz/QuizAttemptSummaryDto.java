package stud.ntnu.backend.dto.quiz;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttemptSummaryDto {
    private Long id;
    private LocalDateTime completedAt;
} 