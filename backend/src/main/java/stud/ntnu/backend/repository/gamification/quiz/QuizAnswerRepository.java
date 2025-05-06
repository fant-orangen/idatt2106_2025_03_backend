package stud.ntnu.backend.repository.gamification.quiz;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.dto.quiz.QuizAnswerDto;
import stud.ntnu.backend.model.gamification.quiz.QuizAnswer;

import java.util.List;

@Repository
public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Long> {
    List<QuizAnswer> findAllByQuestionId(Long questionId);
    @Query("SELECT new stud.ntnu.backend.dto.quiz.QuizAnswerDto(q.id, q.quizId, q.questionId, q.answerBody, q.isCorrect, q.createdAt) " +
        "FROM QuizAnswer q WHERE q.questionId = :questionId")
    List<QuizAnswerDto> findAllAnswersByQuestionId(@Param("questionId") Long questionId);

} 