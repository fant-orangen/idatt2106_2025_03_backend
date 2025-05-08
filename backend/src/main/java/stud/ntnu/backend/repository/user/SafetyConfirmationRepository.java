package stud.ntnu.backend.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stud.ntnu.backend.model.user.SafetyConfirmation;
import stud.ntnu.backend.model.user.User;

/**
 * Repository interface for managing SafetyConfirmation entities in the database.
 * Provides methods for querying and managing safety confirmations.
 * Extends JpaRepository to inherit basic CRUD operations.
 */
@Repository
public interface SafetyConfirmationRepository extends JpaRepository<SafetyConfirmation, Integer> {
    
    /**
     * Finds a safety confirmation for a specific user.
     *
     * @param user The user whose safety confirmation to find
     * @return An Optional containing the found safety confirmation, or empty if none exists
     */
    Optional<SafetyConfirmation> findByUser(User user);

    /**
     * Deletes all safety confirmations associated with a specific user.
     *
     * @param user The user whose safety confirmations to delete
     */
    void deleteByUser(User user);
}