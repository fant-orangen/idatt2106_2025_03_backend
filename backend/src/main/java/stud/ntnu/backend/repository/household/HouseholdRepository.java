package stud.ntnu.backend.repository.household;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.household.Household;
import java.util.Optional;

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
    
    /**
     * Checks if a household with the given name exists.
     * 
     * @param name the name to check
     * @return true if a household with the name exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Finds a household by its name.
     * 
     * @param name the name of the household to find
     * @return Optional containing the household if found, empty otherwise
     */
    Optional<Household> findByName(String name);
}