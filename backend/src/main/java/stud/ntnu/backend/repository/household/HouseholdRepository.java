package stud.ntnu.backend.repository.household;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.household.Household;

/**
 * Repository interface for managing Household entities.
 * Provides basic CRUD operations through JpaRepository and serves as the data access layer
 * for household-related database operations.
 * 
 * @see Household
 * @see JpaRepository
 */
@Repository
public interface HouseholdRepository extends JpaRepository<Household, Integer> {
    // Basic CRUD operations are provided by JpaRepository
}