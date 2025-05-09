package stud.ntnu.backend.repository.group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.group.Group;

/**
 * Repository interface for managing Group entity operations. Provides basic CRUD operations through
 * JpaRepository and custom query methods for specific group-related database operations.
 */
@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {

  /**
   * Checks if a household is currently an active member of a group.
   *
   * @param groupId     The ID of the group to check
   * @param householdId The ID of the household to check
   * @return true if the household is an active member of the group, false otherwise
   */
  @Query(value = """
      SELECT EXISTS (
          SELECT 1 FROM group_memberships gm 
          WHERE gm.group_id = :groupId 
          AND gm.household_id = :householdId 
          AND gm.left_at IS NULL
      )
      """,
      nativeQuery = true)
  boolean existsByIdAndMemberHouseholds_Id(@Param("groupId") Integer groupId,
      @Param("householdId") Integer householdId);
}