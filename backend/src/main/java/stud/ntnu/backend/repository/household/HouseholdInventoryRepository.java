package stud.ntnu.backend.repository.household;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import stud.ntnu.backend.model.household.HouseholdInventory;

/**
 * Repository interface for managing HouseholdInventory entities.
 * Provides data access operations for household inventory items.
 * Extends JpaRepository to inherit basic CRUD operations.
 */
@Repository
public interface HouseholdInventoryRepository extends JpaRepository<HouseholdInventory, Integer> {

    /**
     * Retrieves all inventory items associated with a specific household.
     *
     * @param householdId the unique identifier of the household
     * @return a list of HouseholdInventory items belonging to the specified household
     */
    @Query("SELECT hi FROM HouseholdInventory hi WHERE hi.household.id = :householdId")
    List<HouseholdInventory> findByHouseholdId(@Param("householdId") Integer householdId);
}
