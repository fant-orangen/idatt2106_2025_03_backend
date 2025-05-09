package stud.ntnu.backend.repository.gamification.quiz;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stud.ntnu.backend.model.gamification.quiz.QuizQuestion;

/**
 * Repository interface for managing QuizQuestion entities. Provides methods to interact with quiz
 * questions in the database.
 */
@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {

  /**
   * Counts the number of questions associated with a specific quiz.
   *
   * @param quizId the ID of the quiz to count questions for
   * @return the number of questions in the specified quiz
   */
  long countByQuizId(Long quizId);

  /**
   * Retrieves all questions associated with a specific quiz.
   *
   * @param quizId the ID of the quiz to get questions for
   * @return a list of all questions in the specified quiz
   */
  List<QuizQuestion> findAllByQuizId(Long quizId);
}