package stud.ntnu.backend.repository.gamification.quiz;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import stud.ntnu.backend.model.gamification.quiz.UserQuizAttempt;

@Repository
public interface UserQuizAttemptRepository extends JpaRepository<UserQuizAttempt, Long> {
    Page<UserQuizAttempt> findByUserIdAndQuizIdOrderByIdDesc(Integer userId, Long quizId, Pageable pageable);

    List<UserQuizAttempt> findByUserId(Integer userId);

    @Query("SELECT u FROM UserQuizAttempt u WHERE u.userId = :userId AND u.quizId = :quizId ORDER BY u.id DESC")
    List<UserQuizAttempt> findAllByQuizId(@Param("quizId") Long quizId);

UserQuizAttempt findFirstByUserIdAndQuizIdOrderByIdDesc(Integer userId, Long quizId);
} 