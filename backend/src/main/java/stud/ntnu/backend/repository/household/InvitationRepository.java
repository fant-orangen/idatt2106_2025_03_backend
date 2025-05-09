package stud.ntnu.backend.repository.household;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import stud.ntnu.backend.model.household.Household;
import stud.ntnu.backend.model.household.Invitation;

/**
 * Repository interface for managing Invitation entities in the database. Provides methods for
 * querying and managing household invitations.
 */
@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Integer> {

  /**
   * Retrieves all invitations associated with a specific household.
   *
   * @param household The household to find invitations for
   * @return List of invitations for the specified household
   */
  List<Invitation> findByHousehold(Household household);

  /**
   * Retrieves all invitations sent to a specific email address.
   *
   * @param inviteeEmail The email address to find invitations for
   * @return List of invitations sent to the specified email
   */
  List<Invitation> findByInviteeEmail(String inviteeEmail);

  /**
   * Retrieves all pending invitations for a household that haven't expired. A pending invitation is
   * one that hasn't been accepted or declined.
   *
   * @param household The household to find pending invitations for
   * @param now       The current timestamp for expiration check
   * @return List of pending invitations for the household
   */
  @Query("SELECT i FROM Invitation i WHERE i.household = :household AND i.acceptedAt IS NULL AND i.declinedAt IS NULL AND i.expiresAt > :now")
  List<Invitation> findPendingByHousehold(Household household, LocalDateTime now);

  /**
   * Retrieves all pending invitations for a specific email address that haven't expired.
   *
   * @param email The email address to find pending invitations for
   * @param now   The current timestamp for expiration check
   * @return List of pending invitations for the email address
   */
  @Query("SELECT i FROM Invitation i WHERE i.inviteeEmail = :email AND i.acceptedAt IS NULL AND i.declinedAt IS NULL AND i.expiresAt > :now")
  List<Invitation> findPendingByInviteeEmail(String email, LocalDateTime now);

  /**
   * Checks if a household has any pending invitations that haven't expired.
   *
   * @param household The household to check for pending invitations
   * @param now       The current timestamp for expiration check
   * @return true if there are pending invitations, false otherwise
   */
  @Query("SELECT COUNT(i) > 0 FROM Invitation i WHERE i.household = :household AND i.acceptedAt IS NULL AND i.declinedAt IS NULL AND i.expiresAt > :now")
  boolean hasPendingInvitations(Household household, LocalDateTime now);

  /**
   * Retrieves an invitation by its unique token.
   *
   * @param token The invitation token to search for
   * @return Optional containing the invitation if found
   */
  Optional<Invitation> findByToken(String token);

  /**
   * Checks if there's a pending invitation for a specific email from a specific household.
   *
   * @param household The household to check
   * @param email     The email address to check
   * @param now       The current timestamp for expiration check
   * @return true if there is a pending invitation, false otherwise
   */
  @Query("SELECT COUNT(i) > 0 FROM Invitation i WHERE i.household = :household AND i.inviteeEmail = :email AND i.acceptedAt IS NULL AND i.declinedAt IS NULL AND i.expiresAt > :now")
  boolean hasPendingInvitationForEmail(Household household, String email, LocalDateTime now);
}