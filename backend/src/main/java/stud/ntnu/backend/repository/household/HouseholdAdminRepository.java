package stud.ntnu.backend.repository.household;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import stud.ntnu.backend.model.household.Household;
import stud.ntnu.backend.model.household.HouseholdAdmin;
import stud.ntnu.backend.model.user.User;

/**
 * Repository interface for managing HouseholdAdmin entities.
 * Provides methods to interact with the database for household administration operations.
 */
public interface HouseholdAdminRepository extends JpaRepository<HouseholdAdmin, Integer> {

    /**
     * Checks if a household admin exists for the given user.
     *
     * @param user the user to check for
     * @return true if a household admin exists for the user, false otherwise
     */
    boolean existsByUser(User user);

    /**
     * Finds a household admin by the associated user.
     *
     * @param user the user to search for
     * @return an Optional containing the household admin if found, empty otherwise
     */
    Optional<HouseholdAdmin> findByUser(User user);

    /**
     * Finds all household admins associated with a specific household.
     *
     * @param household the household to search for
     * @return a list of household admins for the specified household
     */
    List<HouseholdAdmin> findByHousehold(Household household);
}