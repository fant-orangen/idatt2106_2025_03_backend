package stud.ntnu.backend.repository.household;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.household.Household;

/**
 * Repository interface for Household entity operations.
 */
@Repository
public interface HouseholdRepository extends JpaRepository<Household, Integer> {
    // Basic CRUD operations are provided by JpaRepository
    // Custom query methods can be added as needed
}