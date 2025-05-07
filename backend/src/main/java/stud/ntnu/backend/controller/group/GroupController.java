package stud.ntnu.backend.controller.group;

/**
 * Manages crisis-supply groups. Supports CRUD operations for groups, listing a user's groups,
 * inviting households to join, and handling group membership changes.
 * <p>
 * Based on Visjonsdokument 2025 for Krisefikser.no.
 */

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import java.security.Principal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import stud.ntnu.backend.dto.group.GroupSummaryDto;
import stud.ntnu.backend.service.group.GroupService;
import stud.ntnu.backend.dto.household.HouseholdDto;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stud.ntnu.backend.model.group.Group;
import stud.ntnu.backend.dto.group.GroupDto;

@RestController
@RequestMapping("/api")
public class GroupController {

  private final GroupService groupService;
  private final Logger logger = LoggerFactory.getLogger(GroupController.class);

  @Autowired
  public GroupController(GroupService groupService) {
    this.groupService = groupService;
  }

  /**
   * Get all groups associated with the current user's household at the moment (paginated).
   *
   * @param principal the authenticated user
   * @param pageable  pagination information
   * @return a paginated list of group summaries
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
   * Remove the current user's household from the given group by setting left_at to now.
   * This endpoint requires admin authentication and is under /api/admin/groups.
   *
   * @param groupId   the group to leave
   * @param principal the authenticated user
   * @return 200 OK if successful, 404 if not found or not a member
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
   * Get all households that currently have a membership in the given group.
   * This endpoint requires admin authentication and is under /api/admin/groups.
   *
   * @param groupId   the group id
   * @param principal the authenticated user
   * @return a list of HouseholdDto
   */
  @GetMapping(path = "/user/groups/{groupId}/households")
  public ResponseEntity<List<HouseholdDto>> getCurrentHouseholdsInGroup(
      @PathVariable("groupId") Integer groupId, Principal principal) {
    String email = principal.getName();
    if (!groupService.isUserMemberOfGroup(groupId, email)) {
      logger.warn("User [{}] attempted to access households of group [{}] without membership.",
          email, groupId);
      return ResponseEntity.status(403).body(null);
    }
    List<HouseholdDto> households = groupService.getCurrentHouseholdsInGroup(groupId);
    return ResponseEntity.ok(households);
  }

  @PostMapping(path = "/user/groups")
  public ResponseEntity<GroupDto> createGroup(@RequestBody GroupDto groupDto, Principal principal) {
    String email = principal.getName();
    Group group = groupService.createGroup(groupDto, email);
    return ResponseEntity.ok(group);
  }

}
