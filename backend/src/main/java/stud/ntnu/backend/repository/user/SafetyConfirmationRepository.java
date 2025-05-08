package stud.ntnu.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import stud.ntnu.backend.model.user.SafetyConfirmation;
import stud.ntnu.backend.model.user.User;
import java.util.Optional;

public interface SafetyConfirmationRepository extends JpaRepository<SafetyConfirmation, Integer> {
    Optional<SafetyConfirmation> findByUser(User user);
} 