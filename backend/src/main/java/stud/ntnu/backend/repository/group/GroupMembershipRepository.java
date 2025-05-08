package stud.ntnu.backend.repository.group;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import stud.ntnu.backend.model.group.GroupMembership;
import stud.ntnu.backend.model.group.GroupMembershipId;

/**
 * Repository interface for managing GroupMembership entities.
 * Provides methods for querying and managing group memberships, including current and historical memberships.
 */
@Repository
public interface GroupMembershipRepository extends JpaRepository<GroupMembership, GroupMembershipId> {
    
    /**
     * Finds the first group membership for a specific household.
     *
     * @param householdId The ID of the household
     * @return An Optional containing the first GroupMembership if found, empty otherwise
     */
    Optional<GroupMembership> findFirstByHousehold_Id(Integer householdId);

    /**
     * Finds the current group membership for a specific household.
     * A membership is considered current if the leftAt date is null or in the future.
     *
     * @param householdId The ID of the household
     * @param now The current timestamp
     * @return An Optional containing the current GroupMembership if found, empty otherwise
     */
    @Query("SELECT gm FROM GroupMembership gm WHERE gm.household.id = :householdId AND (gm.leftAt IS NULL OR gm.leftAt > :now)")
    Optional<GroupMembership> findCurrentByHouseholdId(@Param("householdId") Integer householdId, @Param("now") LocalDateTime now);

    /**
     * Finds all current group memberships for a specific household with pagination.
     * A membership is considered current if the leftAt date is null or in the future.
     *
     * @param householdId The ID of the household
     * @param now The current timestamp
     * @param pageable The pagination information
     * @return A page of current GroupMembership objects
     */
    @Query("SELECT gm FROM GroupMembership gm WHERE gm.household.id = :householdId AND (gm.leftAt IS NULL OR gm.leftAt > :now)")
    Page<GroupMembership> findAllCurrentByHouseholdId(@Param("householdId") Integer householdId, @Param("now") LocalDateTime now, Pageable pageable);

    /**
     * Finds all current group memberships for a specific group.
     * A membership is considered current if the leftAt date is null or in the future.
     *
     * @param groupId The ID of the group
     * @param now The current timestamp
     * @return A list of current GroupMembership objects
     */
    @Query("SELECT gm FROM GroupMembership gm WHERE gm.group.id = :groupId AND (gm.leftAt IS NULL OR gm.leftAt > :now)")
    List<GroupMembership> findAllCurrentByGroupId(@Param("groupId") Integer groupId, @Param("now") LocalDateTime now);

    /**
     * Finds the current group membership for a specific household and group.
     * A membership is considered current if the leftAt date is null or in the future.
     *
     * @param householdId The ID of the household
     * @param groupId The ID of the group
     * @param now The current timestamp
     * @return An Optional containing the current GroupMembership if found, empty otherwise
     */
    @Query("SELECT gm FROM GroupMembership gm WHERE gm.household.id = :householdId AND gm.group.id = :groupId AND (gm.leftAt IS NULL OR gm.leftAt > :now)")
    Optional<GroupMembership> findCurrentByHouseholdIdAndGroupId(@Param("householdId") Integer householdId, @Param("groupId") Integer groupId, @Param("now") LocalDateTime now);

    /**
     * Finds all current group memberships for a specific household where the group is active.
     * A membership is considered current if the leftAt date is null or in the future.
     *
     * @param householdId The ID of the household
     * @param now The current timestamp
     * @param pageable The pagination information
     * @return A page of current GroupMembership objects for active groups
     */
    @Query("SELECT gm FROM GroupMembership gm WHERE gm.household.id = :householdId AND (gm.leftAt IS NULL OR gm.leftAt > :now) AND gm.group.status = 'active'")
    Page<GroupMembership> findAllCurrentByHouseholdIdAndGroupStatus(
        @Param("householdId") Integer householdId, 
        @Param("now") LocalDateTime now,
        Pageable pageable);
}