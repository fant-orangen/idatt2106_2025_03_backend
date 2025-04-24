package stud.ntnu.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.HouseholdInventory;

/**
 * Repository interface for HouseholdInventory entity operations.
 */
@Repository
public interface HouseholdInventoryRepository extends JpaRepository<HouseholdInventory, Integer> {
    // Basic CRUD operations are provided by JpaRepository
    // Custom query methods can be added as needed
}