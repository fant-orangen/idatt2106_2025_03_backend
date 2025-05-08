package stud.ntnu.backend.repository.household;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.household.Household;
import stud.ntnu.backend.model.household.Invitation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Invitation entity operations.
 */
@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Integer> {
    // Find invitations by household
    List<Invitation> findByHousehold(Household household);

    // Find invitations by invitee email
    List<Invitation> findByInviteeEmail(String inviteeEmail);

    // Find pending invitations (not accepted, not declined, not expired)
    @Query("SELECT i FROM Invitation i WHERE i.household = :household AND i.acceptedAt IS NULL AND i.declinedAt IS NULL AND i.expiresAt > :now")
    List<Invitation> findPendingByHousehold(Household household, LocalDateTime now);

    // Find pending invitations for a specific email
    @Query("SELECT i FROM Invitation i WHERE i.inviteeEmail = :email AND i.acceptedAt IS NULL AND i.declinedAt IS NULL AND i.expiresAt > :now")
    List<Invitation> findPendingByInviteeEmail(String email, LocalDateTime now);

    // Check if a household has any pending invitations
    @Query("SELECT COUNT(i) > 0 FROM Invitation i WHERE i.household = :household AND i.acceptedAt IS NULL AND i.declinedAt IS NULL AND i.expiresAt > :now")
    boolean hasPendingInvitations(Household household, LocalDateTime now);

    // Find invitation by token
    Optional<Invitation> findByToken(String token);

    // Check if there's a pending invitation for a specific email from a specific household
    @Query("SELECT COUNT(i) > 0 FROM Invitation i WHERE i.household = :household AND i.inviteeEmail = :email AND i.acceptedAt IS NULL AND i.declinedAt IS NULL AND i.expiresAt > :now")
    boolean hasPendingInvitationForEmail(Household household, String email, LocalDateTime now);
}