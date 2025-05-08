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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import stud.ntnu.backend.dto.group.GroupSummaryDto;
import stud.ntnu.backend.dto.household.HouseholdDto;
import stud.ntnu.backend.service.group.GroupService;

/**
 * REST controller for managing crisis-supply groups.
 * <p>
 * Provides endpoints for:
 * <ul>
 *   <li>Creating new groups</li>
 *   <li>Listing user's current groups</li>
 *   <li>Managing group membership</li>
 *   <li>Viewing group households</li>
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
}
