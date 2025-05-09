package stud.ntnu.backend.repository.gamification.quiz;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import stud.ntnu.backend.model.gamification.quiz.Quiz;

/**
 * Repository interface for managing Quiz entities. Provides methods for retrieving quizzes and
 * quiz-related data.
 */
@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

  /**
   * Retrieves a paginated list of quizzes filtered by status.
   *
   * @param status   The status to filter quizzes by
   * @param pageable The pagination information
   * @return A page of quizzes matching the status
   */
  Page<Quiz> findAllByStatus(String status, Pageable pageable);

  /**
   * Retrieves the name of a quiz by its ID.
   *
   * @param id The ID of the quiz
   * @return An Optional containing the quiz name if found, empty otherwise
   */
  @Query("SELECT q.name FROM Quiz q WHERE q.id = :id")
  Optional<String> findNameById(@Param("id") Long id);
}