package stud.ntnu.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.UserQuizAttempt;

@Repository
public interface UserQuizAttemptRepository extends JpaRepository<UserQuizAttempt, Long> {
    Page<UserQuizAttempt> findByUserIdAndQuizId(Long userId, Long quizId, Pageable pageable);
} 