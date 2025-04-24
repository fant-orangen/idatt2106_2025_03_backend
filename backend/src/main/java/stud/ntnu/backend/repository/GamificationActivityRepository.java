package stud.ntnu.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.information.GamificationActivity;

/**
 * Repository interface for GamificationActivity entity operations.
 */
@Repository
public interface GamificationActivityRepository extends JpaRepository<GamificationActivity, Integer> {
    // Basic CRUD operations are provided by JpaRepository
    // Custom query methods can be added as needed
}