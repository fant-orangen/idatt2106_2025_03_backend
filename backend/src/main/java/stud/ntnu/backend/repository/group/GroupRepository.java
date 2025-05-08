package stud.ntnu.backend.repository.group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.group.Group;

/**
 * Repository interface for Group entity operations.
 */
@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {
    // Basic CRUD operations are provided by JpaRepository
    // Custom query methods can be added as needed

    @Query(value = """
            SELECT EXISTS (
                SELECT 1 FROM group_memberships gm 
                WHERE gm.group_id = :groupId 
                AND gm.household_id = :householdId 
                AND gm.left_at IS NULL
            )
            """, 
            nativeQuery = true)
    boolean existsByIdAndMemberHouseholds_Id(@Param("groupId") Integer groupId, @Param("householdId") Integer householdId);
}