package stud.ntnu.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.user.UserGamificationActivity;

/**
 * Repository interface for UserGamificationActivity entity operations.
 */
@Repository
public interface UserGamificationActivityRepository extends JpaRepository<UserGamificationActivity, Integer> {
    // Basic CRUD operations are provided by JpaRepository
    // Custom query methods can be added as needed
}