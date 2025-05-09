package stud.ntnu.backend.repository.group;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import stud.ntnu.backend.model.group.GroupInvitation;
import stud.ntnu.backend.model.household.Household;

/**
 * Repository interface for managing GroupInvitation entities.
 */
@Repository
public interface GroupInvitationRepository extends JpaRepository<GroupInvitation, Integer> {

  /**
   * Finds all pending invitations for a specific household.
   *
   * @param household the household to find invitations for
   * @param now       the current time to check against expiration
   * @return list of pending invitations
   */
  @Query("SELECT gi FROM GroupInvitation gi WHERE gi.invitedHousehold = :household " +
      "AND gi.acceptedAt IS NULL AND gi.declinedAt IS NULL AND gi.expiresAt > :now")
  List<GroupInvitation> findPendingByHousehold(@Param("household") Household household,
      @Param("now") LocalDateTime now);

  /**
   * Checks if a household has any pending invitations for a specific group.
   *
   * @param groupId     the ID of the group
   * @param householdId the ID of the household
   * @param now         the current time to check against expiration
   * @return true if a pending invitation exists, false otherwise
   */
  @Query("SELECT COUNT(gi) > 0 FROM GroupInvitation gi WHERE gi.group.id = :groupId " +
      "AND gi.invitedHousehold.id = :householdId " +
      "AND gi.acceptedAt IS NULL AND gi.declinedAt IS NULL AND gi.expiresAt > :now")
  boolean existsPendingInvitation(@Param("groupId") Integer groupId,
      @Param("householdId") Integer householdId,
      @Param("now") LocalDateTime now);

  /**
   * Finds all pending invitations for a household by its ID. An invitation is considered pending if
   * it: - Has not been accepted (accepted_at is null) - Has not been declined (declined_at is null)
   * - Has not expired (expires_at is in the future)
   *
   * @param householdId the ID of the household to find invitations for
   * @param now         the current time to check against expiration
   * @return list of pending invitations
   */
  @Query("SELECT gi FROM GroupInvitation gi WHERE gi.invitedHousehold.id = :householdId " +
      "AND gi.acceptedAt IS NULL AND gi.declinedAt IS NULL AND gi.expiresAt > :now")
  List<GroupInvitation> findPendingInvitationsForHousehold(
      @Param("householdId") Integer householdId,
      @Param("now") LocalDateTime now);
} 