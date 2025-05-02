package stud.ntnu.backend.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stud.ntnu.backend.model.UserQuizAttempt;

@Repository
public interface UserQuizAttemptRepository extends JpaRepository<UserQuizAttempt, Long> {
    Page<UserQuizAttempt> findByUserIdAndQuizId(Integer userId, Long quizId, Pageable pageable);
    List<UserQuizAttempt> findByUserId(Integer userId);
} 