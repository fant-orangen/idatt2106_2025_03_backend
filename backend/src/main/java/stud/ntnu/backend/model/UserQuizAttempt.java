package stud.ntnu.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "user_quiz_attempts")
public class UserQuizAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "quiz_id", nullable = false)
    private Long quizId;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public UserQuizAttempt() {}
} 