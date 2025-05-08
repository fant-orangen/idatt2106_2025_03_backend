package stud.ntnu.backend.repository.gamification.quiz;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import stud.ntnu.backend.dto.quiz.QuizAnswerDto;
import stud.ntnu.backend.model.gamification.quiz.QuizAnswer;

/**
 * Repository interface for managing quiz answers in the system.
 * Provides methods to query and retrieve quiz answers from the database.
 */
@Repository
public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Long> {
    
    /**
     * Retrieves all quiz answers associated with a specific question.
     *
     * @param questionId The ID of the question to find answers for
     * @return A list of quiz answers for the specified question
     */
    List<QuizAnswer> findAllByQuestionId(Long questionId);

    /**
     * Retrieves all quiz answers for a specific question as DTOs.
     * This method maps the quiz answers to QuizAnswerDto objects containing
     * only the necessary fields for display.
     *
     * @param questionId The ID of the question to find answers for
     * @return A list of QuizAnswerDto objects for the specified question
     */
    @Query("SELECT new stud.ntnu.backend.dto.quiz.QuizAnswerDto(q.id, q.quizId, q.questionId, q.answerBody, q.isCorrect, q.createdAt) " +
        "FROM QuizAnswer q WHERE q.questionId = :questionId")
    List<QuizAnswerDto> findAllAnswersByQuestionId(@Param("questionId") Long questionId);
}