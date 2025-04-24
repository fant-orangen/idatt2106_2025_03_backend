package stud.ntnu.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.household.Invitation;

/**
 * Repository interface for Invitation entity operations.
 */
@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Integer> {
    // Basic CRUD operations are provided by JpaRepository
    // Custom query methods can be added as needed
}