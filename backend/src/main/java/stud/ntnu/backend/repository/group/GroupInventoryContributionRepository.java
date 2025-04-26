package stud.ntnu.backend.repository.group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.group.GroupInventoryContribution;

/**
 * Repository interface for GroupInventoryContribution entity operations.
 */
@Repository
public interface GroupInventoryContributionRepository extends JpaRepository<GroupInventoryContribution, Integer> {
    // Basic CRUD operations are provided by JpaRepository
    // Custom query methods can be added as needed
}