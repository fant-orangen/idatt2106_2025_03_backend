package stud.ntnu.backend.repository.household;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.household.HouseholdInventory;

import java.util.List;

/**
 * Repository interface for HouseholdInventory entity operations.
 */
@Repository
public interface HouseholdInventoryRepository extends JpaRepository<HouseholdInventory, Integer> {
    // Basic CRUD operations are provided by JpaRepository

    /**
     * Finds all inventory items for a household.
     *
     * @param householdId the ID of the household
     * @return a list of inventory items
     */
    @Query("SELECT hi FROM HouseholdInventory hi WHERE hi.household.id = :householdId")
    List<HouseholdInventory> findByHouseholdId(@Param("householdId") Integer householdId);
}
