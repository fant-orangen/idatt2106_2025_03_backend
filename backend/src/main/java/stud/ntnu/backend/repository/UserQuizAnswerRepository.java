package stud.ntnu.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.UserQuizAnswer;

@Repository
public interface UserQuizAnswerRepository extends JpaRepository<UserQuizAnswer, Long> {
} 