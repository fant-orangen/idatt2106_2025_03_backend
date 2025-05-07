package stud.ntnu.backend.controller.household;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.backend.dto.household.HouseholdCreateRequestDto;
import stud.ntnu.backend.dto.household.HouseholdDto;
import stud.ntnu.backend.dto.household.HouseholdInviteRequestDto;
import stud.ntnu.backend.dto.household.HouseholdInviteResponseDto;
import stud.ntnu.backend.dto.household.HouseholdJoinRequestDto;
import stud.ntnu.backend.dto.household.HouseholdSwitchRequestDto;
import stud.ntnu.backend.dto.household.HouseholdUpdateRequestDto;
import stud.ntnu.backend.model.household.Household;
import stud.ntnu.backend.model.household.Invitation;
import stud.ntnu.backend.service.household.HouseholdService;
import stud.ntnu.backend.service.user.InvitationService;
import stud.ntnu.backend.dto.household.HouseholdMemberDto;
import stud.ntnu.backend.dto.household.EmptyHouseholdMemberDto;
import stud.ntnu.backend.dto.household.EmptyHouseholdMemberCreateDto;
import stud.ntnu.backend.repository.user.UserRepository;

import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * Handles household-level operations. Allows users to create or join households, modify population
 * count (including non-user members), switch households, and retrieve household-related data. Based
 * on Visjonsdokument 2025 for Krisefikser.no.
 */
@RestController
@RequestMapping("/api/user/households")
public class HouseholdController {

  private final HouseholdService householdService;
  private final InvitationService invitationService;
  private final UserRepository userRepository;
  private final Logger log = LoggerFactory.getLogger(HouseholdController.class);

  public HouseholdController(HouseholdService householdService, InvitationService invitationService, UserRepository userRepository) {
    this.householdService = householdService;
    this.invitationService = invitationService;
    this.userRepository = userRepository;
  }

  /**
   * Creates a new household for the authenticated user. Checks if the user already has a household
   * before creating a new one.
   *
   * @param requestDto the household creation request
   * @return ResponseEntity with the created household if successful, or an error message if the
   * user already has a household
   */
  @PostMapping
  public ResponseEntity<?> createHousehold(
      @Valid @RequestBody HouseholdCreateRequestDto requestDto) {
    try {
      Household household = householdService.createHousehold(requestDto);
      return ResponseEntity.ok(household);
    } catch (IllegalStateException e) {
      log.info("Household creation failed: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Invites another user to join the authenticated user's household.
   *
   * @param requestDto the household invitation request
   * @param principal  the Principal object representing the current user
   * @return ResponseEntity with the invitation token if successful, or an error message if the
   * inviter or invitee is not found, or if the inviter doesn't have a household
   */
  @PostMapping("/invite")
  public ResponseEntity<?> inviteToHousehold(
      @Valid @RequestBody HouseholdInviteRequestDto requestDto,
      Principal principal) {
    try {
      HouseholdInviteResponseDto response = householdService.inviteToHousehold(principal.getName(),
          requestDto.getEmail());
      return ResponseEntity.ok(response);
    } catch (IllegalStateException e) {
      log.info("Household invitation failed: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Joins a household using an invitation token.
   *
   * @param requestDto the household join request
   * @param principal  the Principal object representing the current user
   * @return ResponseEntity with the joined household if successful, or an error message if the user
   * is not found, the token is invalid or expired, or the household is not found
   */
  @PostMapping("/join")
  public ResponseEntity<?> joinHousehold(
      @Valid @RequestBody HouseholdJoinRequestDto requestDto,
      Principal principal) {
    try {
      Household household = householdService.joinHousehold(principal.getName(),
          requestDto.getToken());
      return ResponseEntity.ok(household);
    } catch (IllegalStateException e) {
      log.info("Household join failed: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Gets the authenticated user's household.
   *
   * @param principal the Principal object representing the current user
   * @return ResponseEntity with the household if successful, or 404 if the user has no household
   */
  @GetMapping("/me")
  public ResponseEntity<?> getCurrentUserHousehold(Principal principal) {
    try {
      HouseholdDto household = householdService.getCurrentUserHousehold(principal.getName());
      return ResponseEntity.ok(household);
    } catch (IllegalStateException e) {
      log.info("Get household failed: {}", e.getMessage());
      if (e.getMessage().equals("User doesn't have a household")) {
        return ResponseEntity.notFound().build();
      }
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Leaves the current household if the user is not a household admin.
   *
   * @param principal the Principal object representing the current user
   * @return ResponseEntity with success message if successful, or an error message if the user is a
   * household admin or doesn't have a household
   */
  @PostMapping("/leave")
  public ResponseEntity<?> leaveHousehold(Principal principal) {
    try {
      householdService.leaveHousehold(principal.getName());
      return ResponseEntity.ok("Successfully left the household");
    } catch (IllegalStateException e) {
      log.info("Leaving household failed: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Gets all members of the current user's household.
   *
   * @param principal the Principal object representing the current user
   * @return ResponseEntity with the list of household members if successful, or 404 if the user has
   * no household
   */
  @GetMapping("/members")
  public ResponseEntity<?> getHouseholdMembers(Principal principal) {
    try {
      List<HouseholdMemberDto> members = householdService.getHouseholdMembers(principal.getName());
      return ResponseEntity.ok(members);
    } catch (IllegalStateException e) {
      log.info("Get household members failed: {}", e.getMessage());
      if (e.getMessage().equals("User doesn't have a household")) {
        return ResponseEntity.notFound().build();
      }
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Gets only non-admin members of the current user's household.
   *
   * @param principal the Principal object representing the current user
   * @return ResponseEntity with the list of non-admin household members if successful, or 404 if the user has
   * no household
   */
  @GetMapping("/members/non-admin")
  public ResponseEntity<?> getNonAdminHouseholdMembers(Principal principal) {
    try {
      List<HouseholdMemberDto> members = householdService.getNonAdminHouseholdMembers(principal.getName());
      return ResponseEntity.ok(members);
    } catch (IllegalStateException e) {
      log.info("Get non-admin household members failed: {}", e.getMessage());
      if (e.getMessage().equals("User doesn't have a household")) {
        return ResponseEntity.notFound().build();
      }
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Gets all empty members of the current user's household.
   *
   * @param principal the Principal object representing the current user
   * @return ResponseEntity with the list of empty household members if successful, or 404 if the
   * user has no household
   */
  @GetMapping("/members/empty")
  public ResponseEntity<?> getEmptyHouseholdMembers(Principal principal) {
    try {
      List<EmptyHouseholdMemberDto> members = householdService.getEmptyHouseholdMembers(
          principal.getName());
      return ResponseEntity.ok(members);
    } catch (IllegalStateException e) {
      log.info("Get empty household members failed: {}", e.getMessage());
      if (e.getMessage().equals("User doesn't have a household")) {
        return ResponseEntity.notFound().build();
      }
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Adds an empty household member to the current user's household.
   *
   * @param requestDto the empty household member creation request
   * @param principal  the Principal object representing the current user
   * @return ResponseEntity with the created empty household member if successful, or an error
   * message if the user is not found or doesn't have a household
   */
  @PostMapping("/members/empty")
  public ResponseEntity<?> addEmptyHouseholdMember(
      @Valid @RequestBody EmptyHouseholdMemberCreateDto requestDto,
      Principal principal) {
    try {
      EmptyHouseholdMemberDto member = householdService.addEmptyHouseholdMember(principal.getName(),
          requestDto);
      return ResponseEntity.ok(member);
    } catch (IllegalStateException e) {
      log.info("Add empty household member failed: {}", e.getMessage());
      if (e.getMessage().equals("User doesn't have a household")) {
        return ResponseEntity.notFound().build();
      }
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Removes an empty household member from the current user's household.
   *
   * @param memberId  the ID of the empty household member to remove
   * @param principal the Principal object representing the current user
   * @return ResponseEntity with success message if successful, or an error message if the user is not
   * found, doesn't have a household, or the member doesn't belong to the user's household
   */
  @DeleteMapping("/members/empty/{memberId}")
  public ResponseEntity<?> removeEmptyHouseholdMember(
      @PathVariable Integer memberId,
      Principal principal) {
    try {
      householdService.removeEmptyHouseholdMember(principal.getName(), memberId);
      return ResponseEntity.ok("Successfully removed empty household member");
    } catch (IllegalStateException e) {
      log.info("Remove empty household member failed: {}", e.getMessage());
      if (e.getMessage().equals("User doesn't have a household")) {
        return ResponseEntity.notFound().build();
      }
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Gets all pending invitations for the current user's household.
   *
   * @param principal the Principal object representing the current user
   * @return ResponseEntity with the list of pending invitations if successful, or 404 if the user has
   * no household
   */
  @GetMapping("/pending-invitations")
  public ResponseEntity<?> getPendingInvitations(Principal principal) {
    try {
      // Get the user's household ID
      HouseholdDto household = householdService.getCurrentUserHousehold(principal.getName());
      List<Invitation> invitations = householdService.getPendingInvitationsForHousehold(household.getId());
      return ResponseEntity.ok(invitations);
    } catch (IllegalStateException e) {
      log.info("Get pending invitations failed: {}", e.getMessage());
      if (e.getMessage().equals("User doesn't have a household")) {
        return ResponseEntity.notFound().build();
      }
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Cancels a pending invitation by token. Only household admins can cancel invitations.
   * Matches the frontend endpoint: /api/user/households/invitations/{token}
   *
   * @param token the token of the invitation to cancel
   * @param principal the Principal object representing the current user (admin)
   * @return ResponseEntity with success message if successful, or an error message if the user is not
   * found, is not an admin, or the invitation is not found
   */
  @DeleteMapping("/invitations/{token}")
  public ResponseEntity<?> cancelInvitation(
      @PathVariable String token,
      Principal principal) {
    try {
      log.info("Cancel invitation request for token: {}", token);
      householdService.cancelInvitationByToken(principal.getName(), token);
      return ResponseEntity.ok("Successfully canceled invitation");
    } catch (IllegalStateException e) {
      log.error("Cancel invitation failed: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      log.error("Unexpected error canceling invitation: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body("An unexpected error occurred: " + e.getMessage());
    }
  }

  /**
   * Promotes a user to household admin.
   *
   * @param email     the email of the user to promote
   * @param principal the Principal object representing the current user (admin)
   * @return ResponseEntity with success message if successful, or an error message if the admin or
   * user is not found, if the admin is not an admin, or if the user is not in the same household
   */
  @PostMapping("/promote-admin/{email}")
  public ResponseEntity<?> promoteToAdmin(
      @PathVariable String email,
      Principal principal) {
    try {
      householdService.promoteToAdmin(principal.getName(), email);
      return ResponseEntity.ok("Successfully promoted user to admin");
    } catch (IllegalStateException e) {
      log.info("Promote to admin failed: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Removes a member from the household. Only household admins can remove members.
   *
   * @param memberId  the ID of the member to remove
   * @param principal the Principal object representing the current user (admin)
   * @return ResponseEntity with success message if successful, or an error message if the admin or
   * member is not found, if the admin is not an admin, if the member is not in the same household,
   * or if the member is the last admin
   */
  @DeleteMapping("/members/{memberId}")
  public ResponseEntity<?> removeMemberFromHousehold(
      @PathVariable Integer memberId,
      Principal principal) {
    try {
      householdService.removeMemberFromHousehold(principal.getName(), memberId);
      return ResponseEntity.ok("Successfully removed member from household");
    } catch (IllegalStateException e) {
      log.info("Remove member from household failed: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Checks if the current user is an admin of their household.
   *
   * @param principal the Principal object representing the current user
   * @return ResponseEntity with a JSON object containing isAdmin field
   */
  @GetMapping("/is-admin")
  public ResponseEntity<?> isCurrentUserHouseholdAdmin(Principal principal) {
    try {
      // Find the user by email
      String email = principal.getName();
      stud.ntnu.backend.model.user.User user = userRepository.findByEmail(email)
          .orElseThrow(() -> new IllegalStateException("User not found"));

      // Check if the user is a household admin
      boolean isAdmin = householdService.isUserHouseholdAdmin(user);

      // Return the result as a JSON object
      return ResponseEntity.ok(Map.of("isAdmin", isAdmin));
    } catch (Exception e) {
      log.error("Error checking admin status: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Soft deletes the current user's household. Only household admins can delete households.
   * This marks the household as deleted but keeps the data in the database.
   *
   * @param principal the Principal object representing the current user (admin)
   * @return ResponseEntity with success message if successful, or an error message if the user is not
   * found, doesn't have a household, or is not an admin
   */
  @DeleteMapping("/delete")
  public ResponseEntity<?> deleteHousehold(Principal principal) {
    try {
      householdService.deleteCurrentHousehold(principal.getName());
      return ResponseEntity.ok("Successfully deleted household");
    } catch (IllegalStateException e) {
      log.info("Delete household failed: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(500).body("An unexpected error occurred: " + e.getMessage());
    }
  }

  /**
   * Updates the current user's household name and address. Only household admins can update households.
   *
   * @param updateRequestDto the household update request containing the new name and address
   * @param principal the Principal object representing the current user (admin)
   * @return ResponseEntity with the updated household if successful, or an error message if the user is not
   * found, doesn't have a household, or is not an admin
   */
  @PutMapping("/update")
  public ResponseEntity<?> updateHousehold(
      @Valid @RequestBody HouseholdUpdateRequestDto updateRequestDto,
      Principal principal) {
    try {
      log.info("Update household request from user: {}", principal.getName());
      Household updatedHousehold = householdService.updateHousehold(
          principal.getName(),
          updateRequestDto.getName(),
          updateRequestDto.getAddress());

      // Convert to DTO for response
      HouseholdDto householdDto = new HouseholdDto(
          updatedHousehold.getId(),
          updatedHousehold.getName(),
          updatedHousehold.getAddress(),
          updatedHousehold.getPopulationCount(),
          updatedHousehold.getLatitude(),
          updatedHousehold.getLongitude());

      return ResponseEntity.ok(householdDto);
    } catch (IllegalStateException e) {
      log.error("Update household failed due to validation error: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      log.error("Unexpected error updating household: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body("An unexpected error occurred: " + e.getMessage());
    }
  }
}
