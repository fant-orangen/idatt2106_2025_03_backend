package stud.ntnu.backend.repository.gamification.quiz;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.gamification.quiz.UserQuizAttempt;

/**
 * Repository interface for managing UserQuizAttempt entities. Provides methods to query and
 * retrieve quiz attempts by users.
 */
@Repository
public interface UserQuizAttemptRepository extends JpaRepository<UserQuizAttempt, Long> {

  /**
   * Finds paginated quiz attempts for a specific user and quiz, ordered by ID in descending order.
   *
   * @param userId   The ID of the user
   * @param quizId   The ID of the quiz
   * @param pageable The pagination information
   * @return A page of UserQuizAttempt objects
   */
  Page<UserQuizAttempt> findByUserIdAndQuizIdOrderByIdDesc(Integer userId, Long quizId,
      Pageable pageable);

  /**
   * Finds all quiz attempts for a specific user.
   *
   * @param userId The ID of the user
   * @return A list of UserQuizAttempt objects
   */
  List<UserQuizAttempt> findByUserId(Integer userId);

  /**
   * Finds all quiz attempts for a specific quiz, ordered by ID in descending order.
   *
   * @param quizId The ID of the quiz
   * @return A list of UserQuizAttempt objects
   */
  @Query("SELECT u FROM UserQuizAttempt u WHERE u.userId = :userId AND u.quizId = :quizId ORDER BY u.id DESC")
  List<UserQuizAttempt> findAllByQuizId(@Param("quizId") Long quizId);

  /**
   * Finds the most recent quiz attempt for a specific user and quiz.
   *
   * @param userId The ID of the user
   * @param quizId The ID of the quiz
   * @return The most recent UserQuizAttempt object
   */
  UserQuizAttempt findFirstByUserIdAndQuizIdOrderByIdDesc(Integer userId, Long quizId);
}