package stud.ntnu.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.PointOfInterest;

/**
 * Repository interface for PointOfInterest entity operations.
 */
@Repository
public interface PointOfInterestRepository extends JpaRepository<PointOfInterest, Integer> {
    // Basic CRUD operations are provided by JpaRepository
    // Custom query methods can be added as needed
}