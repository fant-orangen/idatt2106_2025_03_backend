package stud.ntnu.backend.repository.gamification.quiz;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.gamification.quiz.UserQuizAnswer;
import java.util.List;

@Repository
public interface UserQuizAnswerRepository extends JpaRepository<UserQuizAnswer, Long> {
    List<UserQuizAnswer> findAllByUserQuizAttemptId(Long userQuizAttemptId);
} 