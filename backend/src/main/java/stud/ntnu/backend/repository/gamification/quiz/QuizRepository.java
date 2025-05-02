package stud.ntnu.backend.repository.gamification.quiz;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.Quiz;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
} 