package stud.ntnu.backend.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

// TODO: Add JPA annotations (@Entity, @Table, @Id, etc.) when dependencies are available
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserQuizAttempt {
    private Long id;
    private Long userId;
    private Long quizId;
    private LocalDateTime completedAt;
} 