package stud.ntnu.backend.repository.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import stud.ntnu.backend.model.household.Household;
import stud.ntnu.backend.model.user.User;

/**
 * Repository interface for User entity operations. Provides methods to find users by email and
 * other criteria.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Find a user by their email address.
     *
     * @param email the email address to search for
     * @return an Optional containing the user if found, or empty if not found
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if a user exists with the given email address.
     *
     * @param email the email address to check
     * @return true if a user exists with the email, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Find all users belonging to a specific household.
     *
     * @param household the household to search for users in
     * @return a list of users in the specified household
     */
    List<User> findByHousehold(Household household);

    /**
     * Find all users belonging to a household with the specified ID.
     *
     * @param householdId the ID of the household to search for users in
     * @return a list of users in the specified household
     */
    List<User> findByHouseholdId(Integer householdId);

    /**
     * Count the number of users in a household.
     *
     * @param householdId the ID of the household to count users in
     * @return the number of users in the specified household
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.household.id = :householdId")
    Integer countByHouseholdId(@Param("householdId") Integer householdId);

    /**
     * Calculate the sum of calorie requirements for all users in a household.
     *
     * @param householdId the ID of the household to calculate total calories for
     * @return the sum of calorie requirements for all users in the household, or 0 if none exist
     */
    @Query("SELECT COALESCE(SUM(u.kcalRequirement), 0) FROM User u WHERE u.household.id = :householdId")
    Integer sumKcalRequirementByHouseholdId(@Param("householdId") Integer householdId);
}