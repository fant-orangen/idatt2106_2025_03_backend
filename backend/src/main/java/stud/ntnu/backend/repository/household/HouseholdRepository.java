package stud.ntnu.backend.repository.household;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.household.Household;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Household entity operations.
 */
@Repository
public interface HouseholdRepository extends JpaRepository<Household, Integer> {
    // Find by ID and not deleted
    Optional<Household> findByIdAndDeletedFalse(Integer id);

    // Find all not deleted
    List<Household> findByDeletedFalse();

    // Check if a household exists and is not deleted
    boolean existsByIdAndDeletedFalse(Integer id);
}