package stud.ntnu.backend.controller.group;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import stud.ntnu.backend.dto.group.GroupInviteRequestDto;
import stud.ntnu.backend.dto.group.GroupSummaryDto;
import stud.ntnu.backend.dto.household.HouseholdDto;
import stud.ntnu.backend.service.group.GroupService;
import stud.ntnu.backend.model.group.GroupInvitation;

/**
 * REST controller for managing crisis-supply groups.
 * <p>
 * Provides endpoints for:
 * <ul>
 *   <li>Creating new groups</li>
 *   <li>Listing user's current groups</li>
 *   <li>Managing group membership</li>
 *   <li>Viewing group households</li>
 *   <li>Inviting households to groups</li>
 *   <li>Accepting and declining invitations</li>
 *   <li>Retrieving pending invitations</li>
 * </ul>
 * <p>
 */
@RestController
@RequestMapping("/api")
public class GroupController {

  private final GroupService groupService;

  @Autowired
  public GroupController(GroupService groupService) {
    this.groupService = groupService;
  }

  /**
   * Retrieves all groups associated with the current user's household.
   * Results are paginated and include basic group information.
   *
   * @param principal the authenticated user making the request
   * @param pageable pagination parameters (page number, size, sorting)
   * @return ResponseEntity containing:
   *         - 200 OK with paginated list of GroupSummaryDto if groups exist
   *         - 404 Not Found if no groups exist
   */
  @GetMapping("/user/groups/current")
  public ResponseEntity<Page<GroupSummaryDto>> getCurrentUserGroups(Principal principal,
      Pageable pageable) {
    String email = principal.getName();
    Page<GroupSummaryDto> groups = groupService.getCurrentUserGroups(email, pageable);
    if (groups.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(groups);
  }

  /**
   * Removes the current user's household from a specified group.
   * This is a soft delete that sets the left_at timestamp.
   *
   * @param groupId the ID of the group to leave
   * @param principal the authenticated user making the request
   * @return ResponseEntity containing:
   *         - 200 OK if removal was successful
   *         - 404 Not Found if group doesn't exist or user is not a member
   */
  @PatchMapping(path = "/user/groups/leave/{groupid}")
  public ResponseEntity<?> removeHouseholdFromGroup(@PathVariable("groupid") Integer groupId,
      Principal principal) {
    String email = principal.getName();
    boolean removed = groupService.removeHouseholdFromGroup(email, groupId);
    if (!removed) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok().build();
  }

  /**
   * Retrieves all households currently active in a specified group.
   * Requires the requesting user to be a member of the group.
   *
   * @param groupId the ID of the group to get households for
   * @param principal the authenticated user making the request
   * @return ResponseEntity containing:
   *         - 200 OK with list of HouseholdDto if successful
   *         - 403 Forbidden if user is not a member of the group
   */
  @GetMapping(path = "/user/groups/{groupId}/households")
  public ResponseEntity<List<HouseholdDto>> getCurrentHouseholdsInGroup(
      @PathVariable("groupId") Integer groupId, Principal principal) {
    String email = principal.getName();
    if (!groupService.isUserMemberOfGroup(groupId, email)) {
      return ResponseEntity.status(403).body(null);
    }
    List<HouseholdDto> households = groupService.getCurrentHouseholdsInGroup(groupId);
    return ResponseEntity.ok(households);
  }
 
  /**
   * Creates a new group with the specified name.
   * Requires the requesting user to have household admin privileges.
   *
   * @param name the name to give the new group
   * @param principal the authenticated user making the request
   * @return ResponseEntity containing:
   *         - 200 OK if group creation was successful
   *         - 403 Forbidden if user lacks household admin privileges
   */
  @PostMapping(path = "/user/groups/{name}")
  public ResponseEntity<?> createGroup(@PathVariable("name") String name, Principal principal) {
    String email = principal.getName();
    boolean created = groupService.createGroup(name, email);
    if (!created) {
      return ResponseEntity.status(403).build();
    }
    return ResponseEntity.ok().build();
  }

  /**
   * Invites a household to join a group.
   * Requires the requesting user to be a member of the group.
   *
   * @param requestDto the DTO containing the household name and group ID
   * @param principal the authenticated user making the request
   * @return ResponseEntity containing:
   *         - 200 OK if invitation was sent successfully
   *         - 403 Forbidden if user is not a member of the group
   *         - 404 Not Found if household or group doesn't exist
   */
  @PostMapping("/user/groups/invite")
  public ResponseEntity<?> inviteHouseholdToGroup(
      @RequestBody @Valid GroupInviteRequestDto requestDto,
      Principal principal) {
    String email = principal.getName();
    if (!groupService.isUserMemberOfGroup(requestDto.getGroupId(), email)) {
      return ResponseEntity.status(403).build();
    }
    
    try {
      boolean invited = groupService.inviteHouseholdToGroup(
          requestDto.getHouseholdName(),
          requestDto.getGroupId(),
          email);
      return invited ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    } catch (IllegalStateException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Retrieves all pending invitations for the current user.
   * @param principal the authenticated user making the request
   * @return ResponseEntity containing:
   *         - 200 OK with list of GroupInvitation if successful
   *         - 404 Not Found if no pending invitations exist
   */
  @GetMapping("/user/groups/invitations")
  public ResponseEntity<?> getPendingInvitations(Principal principal) {
    String email = principal.getName();
    List<GroupInvitation> invitations = groupService.getPendingInvitations(email);
    return ResponseEntity.ok(invitations);
  }

  /**
   * Accepts a group invitation.
   * Requires the user to be a member of the invited household.
   *
   * @param invitationId the ID of the invitation to accept
   * @param principal the authenticated user making the request
   * @return ResponseEntity containing:
   *         - 200 OK if invitation was accepted successfully
   *         - 403 Forbidden if user is not a member of the invited household
   *         - 404 Not Found if invitation doesn't exist or is not pending
   */
  @PatchMapping("/user/groups/invitations/{invitationId}/accept")
  public ResponseEntity<?> acceptInvitation(
      @PathVariable("invitationId") Integer invitationId,
      Principal principal) {
    String email = principal.getName();
    try {
      boolean accepted = groupService.acceptInvitation(invitationId, email);
      return accepted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    } catch (IllegalStateException e) {
      return ResponseEntity.status(403).body(e.getMessage());
    }
  }

  /**
   * Rejects a group invitation.
   * Requires the user to be a member of the invited household.
   *
   * @param invitationId the ID of the invitation to reject
   * @param principal the authenticated user making the request
   * @return ResponseEntity containing:
   *         - 200 OK if invitation was rejected successfully
   *         - 403 Forbidden if user is not a member of the invited household
   *         - 404 Not Found if invitation doesn't exist or is not pending
   */
  @PatchMapping("/user/groups/invitations/{invitationId}/reject")
  public ResponseEntity<?> rejectInvitation(
      @PathVariable("invitationId") Integer invitationId,
      Principal principal) {
    String email = principal.getName();
    try {
      boolean rejected = groupService.rejectInvitation(invitationId, email);
      return rejected ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    } catch (IllegalStateException e) {
      return ResponseEntity.status(403).body(e.getMessage());
    }
  }

}
