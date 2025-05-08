package stud.ntnu.backend.controller.user;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import stud.ntnu.backend.dto.household.HouseholdJoinRequestDto;
import stud.ntnu.backend.model.household.Household;
import stud.ntnu.backend.model.household.Invitation;
import stud.ntnu.backend.service.household.HouseholdService;
import stud.ntnu.backend.service.user.InvitationService;

/**
 * Controller for managing household and group invitations.
 * <p>
 * This controller handles the complete invitation workflow including:
 * - Retrieving pending invitations
 * - Accepting invitations
 * - Declining invitations
 * <p>
 * All endpoints require authentication and operate on behalf of the authenticated user.
 * Based on Visjonsdokument 2025 for Krisefikser.no.
 */
@RestController
@RequestMapping("/api/user/invitations")
public class InvitationController {

  private final InvitationService invitationService;
  private final HouseholdService householdService;

  /**
   * Constructs a new InvitationController with required services.
   *
   * @param invitationService service for managing invitations
   * @param householdService service for managing households
   */
  public InvitationController(InvitationService invitationService, HouseholdService householdService) {
    this.invitationService = invitationService;
    this.householdService = householdService;
  }

  /**
   * Retrieves all pending invitations for the authenticated user.
   *
   * @param principal the Principal object representing the current user
   * @return ResponseEntity containing a list of pending invitations if successful,
   *         or an error message if the operation fails
   */
  @GetMapping("/pending")
  public ResponseEntity<?> getPendingInvitations(Principal principal) {
    try {
      List<Invitation> invitations = invitationService.getPendingInvitationsForUser(principal.getName());
      return ResponseEntity.ok(invitations);
    } catch (IllegalStateException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Accepts a household invitation using the provided token.
   *
   * @param requestDto the household join request containing the invitation token
   * @param principal the Principal object representing the current user
   * @return ResponseEntity containing the joined household if successful,
   *         or an error message if the user is not found, token is invalid/expired,
   *         or the household is not found
   */
  @PostMapping("/accept")
  public ResponseEntity<?> acceptInvitation(
      @Valid @RequestBody HouseholdJoinRequestDto requestDto,
      Principal principal) {
    try {
      Household household = householdService.joinHousehold(principal.getName(), requestDto.getToken());
      return ResponseEntity.ok(household);
    } catch (IllegalStateException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Declines a household invitation using the provided token.
   *
   * @param requestDto the household join request containing the invitation token
   * @param principal the Principal object representing the current user
   * @return ResponseEntity containing a success message if the invitation is declined,
   *         or an error message if the user is not found or token is invalid/expired
   */
  @PostMapping("/decline")
  public ResponseEntity<?> declineInvitation(
      @Valid @RequestBody HouseholdJoinRequestDto requestDto,
      Principal principal) {
    try {
      invitationService.declineInvitation(principal.getName(), requestDto.getToken());
      return ResponseEntity.ok("Successfully declined the invitation");
    } catch (IllegalStateException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}
