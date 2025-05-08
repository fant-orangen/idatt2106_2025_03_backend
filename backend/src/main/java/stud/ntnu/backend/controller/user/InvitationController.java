package stud.ntnu.backend.controller.user;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.backend.dto.household.HouseholdJoinRequestDto;
import stud.ntnu.backend.model.household.Household;
import stud.ntnu.backend.model.household.Invitation;
import stud.ntnu.backend.service.household.HouseholdService;
import stud.ntnu.backend.service.user.InvitationService;

import java.security.Principal;
import java.util.List;

/**
 * Handles invitation workflows for households and groups. Supports generating and accepting invite
 * tokens, validating token expiration, and tracking invite status.
 * <p>
 * Based on Visjonsdokument 2025 for Krisefikser.no.
 */
@RestController
@RequestMapping("/api/user/invitations")
public class InvitationController {

  private final InvitationService invitationService;
  private final HouseholdService householdService;
  private final Logger log = LoggerFactory.getLogger(InvitationController.class);

  public InvitationController(InvitationService invitationService, HouseholdService householdService) {
    this.invitationService = invitationService;
    this.householdService = householdService;
  }

  /**
   * Gets all pending invitations for the current user.
   *
   * @param principal the Principal object representing the current user
   * @return ResponseEntity with the list of pending invitations
   */
  @GetMapping("/pending")
  public ResponseEntity<?> getPendingInvitations(Principal principal) {
    try {
      List<Invitation> invitations = invitationService.getPendingInvitationsForUser(principal.getName());
      return ResponseEntity.ok(invitations);
    } catch (IllegalStateException e) {
      log.info("Get pending invitations failed: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Accepts a household invitation.
   *
   * @param requestDto the household join request (contains the token)
   * @param principal  the Principal object representing the current user
   * @return ResponseEntity with the joined household if successful, or an error message if the user
   * is not found, the token is invalid or expired, or the household is not found
   */
  @PostMapping("/accept")
  public ResponseEntity<?> acceptInvitation(
      @Valid @RequestBody HouseholdJoinRequestDto requestDto,
      Principal principal) {
    try {
      Household household = householdService.joinHousehold(principal.getName(), requestDto.getToken());
      return ResponseEntity.ok(household);
    } catch (IllegalStateException e) {
      log.info("Accept invitation failed: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Declines a household invitation.
   *
   * @param requestDto the household join request (contains the token)
   * @param principal  the Principal object representing the current user
   * @return ResponseEntity with success message if successful, or an error message if the user is not
   * found, the token is invalid or expired
   */
  @PostMapping("/decline")
  public ResponseEntity<?> declineInvitation(
      @Valid @RequestBody HouseholdJoinRequestDto requestDto,
      Principal principal) {
    try {
      invitationService.declineInvitation(principal.getName(), requestDto.getToken());
      return ResponseEntity.ok("Successfully declined the invitation");
    } catch (IllegalStateException e) {
      log.info("Decline invitation failed: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}
