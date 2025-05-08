package stud.ntnu.backend.model.gamification.quiz;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * Entity class representing a user's attempt to complete a quiz.
 * This class maps to the 'user_quiz_attempts' table in the database and tracks
 * when users start and complete quizzes.
 */
@Setter
@Getter
@Entity
@Table(name = "user_quiz_attempts")
public class UserQuizAttempt {
    /**
     * Unique identifier for the quiz attempt.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID of the user attempting the quiz.
     * This field cannot be null.
     */
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    /**
     * ID of the quiz being attempted.
     * This field cannot be null.
     */
    @Column(name = "quiz_id", nullable = false)
    private Long quizId;

    /**
     * Timestamp when the quiz attempt was completed.
     * This field can be null if the quiz attempt is still in progress.
     */
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /**
     * Default constructor required by JPA.
     */
    public UserQuizAttempt() {}
}