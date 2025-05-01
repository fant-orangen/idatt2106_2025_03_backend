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
import stud.ntnu.backend.dto.group.GroupSummaryDto;
import stud.ntnu.backend.service.GroupService;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

  private final GroupService groupService;

  @Autowired
  public GroupController(GroupService groupService) {
    this.groupService = groupService;
  }

  /**
   * Get the group associated with the current user's household.
   * @param principal the authenticated user
   * @return the group summary (id, name, createdAt)
   */
  @GetMapping("/current")
  public ResponseEntity<GroupSummaryDto> getCurrentUserGroup(Principal principal) {
    String email = principal.getName();
    GroupSummaryDto group = groupService.getCurrentUserGroup(email);
    if (group == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(group);
  }
}
