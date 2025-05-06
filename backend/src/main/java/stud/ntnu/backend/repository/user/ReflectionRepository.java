package stud.ntnu.backend.repository.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.user.Reflection;
import stud.ntnu.backend.model.user.User;

import java.util.List;

/**
 * Repository interface for Reflection entity operations.
 */
@Repository
public interface ReflectionRepository extends JpaRepository<Reflection, Integer> {
    // Basic CRUD operations are provided by JpaRepository

    /**
     * Find all non-deleted reflections for a specific user.
     *
     * @param userId the ID of the user
     * @return a list of reflections
     */
    List<Reflection> findByUserIdAndDeletedFalse(Integer userId);

    /**
     * Find all non-deleted reflections for a specific user with pagination.
     *
     * @param userId the ID of the user
     * @param pageable pagination information
     * @return a page of reflections
     */
    Page<Reflection> findByUserIdAndDeletedFalse(Integer userId, Pageable pageable);

    /**
     * Find all shared and non-deleted reflections for users in a specific household.
     *
     * @param householdId the ID of the household
     * @param pageable pagination information
     * @return a page of shared reflections
     */
    @Query("SELECT r FROM Reflection r WHERE r.shared = true AND r.deleted = false AND r.user.household.id = :householdId")
    Page<Reflection> findSharedByHouseholdId(@Param("householdId") Integer householdId, Pageable pageable);

    /**
     * Find all shared and non-deleted reflections for users in households that are members of a specific group.
     *
     * @param groupId the ID of the group
     * @param pageable pagination information
     * @return a page of shared reflections
     */
    @Query("SELECT r FROM Reflection r JOIN r.user u JOIN u.household h JOIN GroupMembership gm ON gm.household.id = h.id " +
           "WHERE r.shared = true AND r.deleted = false AND gm.group.id = :groupId AND (gm.leftAt IS NULL OR gm.leftAt > CURRENT_TIMESTAMP)")
    Page<Reflection> findSharedByGroupId(@Param("groupId") Integer groupId, Pageable pageable);

    /**
     * Find all shared and non-deleted reflections that are visible to a specific user (from their household and groups).
     *
     * @param userId the ID of the user
     * @param pageable pagination information
     * @return a page of shared reflections
     */
    @Query("SELECT DISTINCT r FROM Reflection r " +
           "WHERE r.shared = true AND r.deleted = false AND (" +
           "  (r.user.household.id = (SELECT u.household.id FROM User u WHERE u.id = :userId)) OR " +
           "  (r.user.household.id IN (" +
           "    SELECT gm.household.id FROM GroupMembership gm " +
           "    WHERE gm.group.id IN (" +
           "      SELECT gm2.group.id FROM GroupMembership gm2 " +
           "      WHERE gm2.household.id = (SELECT u.household.id FROM User u WHERE u.id = :userId) " +
           "      AND (gm2.leftAt IS NULL OR gm2.leftAt > CURRENT_TIMESTAMP)" +
           "    ) " +
           "    AND (gm.leftAt IS NULL OR gm.leftAt > CURRENT_TIMESTAMP)" +
           "  ))" +
           ") AND r.user.id != :userId")
    Page<Reflection> findSharedVisibleToUser(@Param("userId") Integer userId, Pageable pageable);
}