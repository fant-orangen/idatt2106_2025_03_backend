package stud.ntnu.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.map.CrisisEvent;

/**
 * Repository interface for CrisisEvent entity operations.
 */
@Repository
public interface CrisisEventRepository extends JpaRepository<CrisisEvent, Integer> {
    // Basic CRUD operations are provided by JpaRepository
    // Custom query methods can be added as needed
}