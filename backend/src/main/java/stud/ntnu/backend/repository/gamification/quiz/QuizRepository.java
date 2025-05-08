package stud.ntnu.backend.repository.gamification.quiz;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.gamification.quiz.Quiz;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Page<Quiz> findAllByStatus(String status, Pageable pageable);
    @Query("SELECT q.name FROM Quiz q WHERE q.id = :id")
    Optional<String> findNameById(@Param("id") Long id);
} 