package stud.ntnu.backend.repository.gamification.quiz;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stud.ntnu.backend.model.gamification.quiz.UserQuizAnswer;

/**
 * Repository interface for managing UserQuizAnswer entities. Provides methods to interact with the
 * database for quiz answer operations.
 */
@Repository
public interface UserQuizAnswerRepository extends JpaRepository<UserQuizAnswer, Long> {

  /**
   * Retrieves all quiz answers associated with a specific quiz attempt.
   *
   * @param userQuizAttemptId the ID of the quiz attempt
   * @return a list of UserQuizAnswer entities for the specified attempt
   */
  List<UserQuizAnswer> findAllByUserQuizAttemptId(Long userQuizAttemptId);
}