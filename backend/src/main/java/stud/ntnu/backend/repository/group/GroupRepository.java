package stud.ntnu.backend.repository.group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.group.Group;

/**
 * Repository interface for Group entity operations.
 */
@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {
    // Basic CRUD operations are provided by JpaRepository
    // Custom query methods can be added as needed
}