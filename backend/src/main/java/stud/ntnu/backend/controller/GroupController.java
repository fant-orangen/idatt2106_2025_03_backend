package stud.ntnu.backend.controller;

/**
 * Manages crisis-supply groups. Supports CRUD operations for groups, listing a user's groups,
 * inviting households to join, and handling group membership changes.
 * <p>
 * Based on Visjonsdokument 2025 for Krisefikser.no.
 */

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.security.Principal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import stud.ntnu.backend.dto.group.GroupSummaryDto;
import stud.ntnu.backend.service.GroupService;
import stud.ntnu.backend.dto.household.HouseholdDto;
import java.util.List;
import stud.ntnu.backend.dto.inventory.ProductTypeDto;
import java.util.Objects;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

  private final GroupService groupService;

  @Autowired
  public GroupController(GroupService groupService) {
    this.groupService = groupService;
  }

  /**
   * Get all groups associated with the current user's household at the moment (paginated).
   * TODO: untested
   * @param principal the authenticated user
   * @param pageable pagination information
   * @return a paginated list of group summaries
   */
  @GetMapping("/current")
  public ResponseEntity<Page<GroupSummaryDto>> getCurrentUserGroups(Principal principal, Pageable pageable) {
    String email = principal.getName();
    Page<GroupSummaryDto> groups = groupService.getCurrentUserGroups(email, pageable);
    if (groups.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(groups);
  }

  /**
   * Remove the current user's household from the given group by setting left_at to now.
   * TODO: untested
   * @param groupId the group to leave
   * @param principal the authenticated user
   * @return 200 OK if successful, 404 if not found or not a member
   */
  @PatchMapping("/leave/{groupid}")
  public ResponseEntity<?> removeHouseholdFromGroup(@PathVariable("groupid") Integer groupId, Principal principal) {
    String email = principal.getName();
    boolean removed = groupService.removeHouseholdFromGroup(email, groupId);
    if (!removed) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok().build();
  }

  /**
   * Get all households that currently have a membership in the given group.
   * TODO: untested
   * @param groupId the group id
   * @param principal the authenticated user
   * @return a list of HouseholdDto
   */
  @GetMapping("/{groupId}/households")
  public ResponseEntity<List<HouseholdDto>> getCurrentHouseholdsInGroup(@PathVariable("groupId") Integer groupId, Principal principal) {
    String email = principal.getName();
    if (!groupService.isUserMemberOfGroup(groupId, email)) {
      return ResponseEntity.status(403).body(null);
    }
    List<HouseholdDto> households = groupService.getCurrentHouseholdsInGroup(groupId);
    return ResponseEntity.ok(households);
  }

}
