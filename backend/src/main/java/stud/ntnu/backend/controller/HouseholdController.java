package stud.ntnu.backend.controller;

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
import stud.ntnu.backend.model.household.Household;
import stud.ntnu.backend.service.household.HouseholdService;
import stud.ntnu.backend.dto.household.HouseholdMemberDto;
import stud.ntnu.backend.dto.household.EmptyHouseholdMemberDto;
import stud.ntnu.backend.dto.household.EmptyHouseholdMemberCreateDto;

import java.security.Principal;
import java.util.List;

/**
 * Handles household-level operations. Allows users to create or join households, modify population
 * count (including non-user members), switch households, and retrieve household-related data.
 * Based on Visjonsdokument 2025 for Krisefikser.no.
 */
@RestController
@RequestMapping("/api/households")
public class HouseholdController {

  private final HouseholdService householdService;
  private final Logger log = LoggerFactory.getLogger(HouseholdController.class);

  public HouseholdController(HouseholdService householdService) {
    this.householdService = householdService;
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
   * Switches the authenticated user to a different household.
   *
   * @param requestDto the household switch request
   * @param principal the Principal object representing the current user
   * @return ResponseEntity with the updated household if successful, or an error message if the
   * user or household is not found
   */
  @PutMapping("/switch")
  public ResponseEntity<?> switchHousehold(
      @Valid @RequestBody HouseholdSwitchRequestDto requestDto,
      Principal principal) {
    try {
      Household household = householdService.switchHousehold(principal.getName(), requestDto.getHouseholdId());
      return ResponseEntity.ok(household);
    } catch (IllegalStateException e) {
      log.info("Household switch failed: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Invites another user to join the authenticated user's household.
   *
   * @param requestDto the household invitation request
   * @param principal the Principal object representing the current user
   * @return ResponseEntity with the invitation token if successful, or an error message if the
   * inviter or invitee is not found, or if the inviter doesn't have a household
   */
  @PostMapping("/invite")
  public ResponseEntity<?> inviteToHousehold(
      @Valid @RequestBody HouseholdInviteRequestDto requestDto,
      Principal principal) {
    try {
      HouseholdInviteResponseDto response = householdService.inviteToHousehold(principal.getName(), requestDto.getEmail());
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
   * @param principal the Principal object representing the current user
   * @return ResponseEntity with the joined household if successful, or an error message if the
   * user is not found, the token is invalid or expired, or the household is not found
   */
  @PostMapping("/join")
  public ResponseEntity<?> joinHousehold(
      @Valid @RequestBody HouseholdJoinRequestDto requestDto,
      Principal principal) {
    try {
      Household household = householdService.joinHousehold(principal.getName(), requestDto.getToken());
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
   * @return ResponseEntity with success message if successful, or an error message if the
   * user is a household admin or doesn't have a household
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
   * @return ResponseEntity with the list of household members if successful, or 404 if the user has no household
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
   * Gets all empty members of the current user's household.
   *
   * @param principal the Principal object representing the current user
   * @return ResponseEntity with the list of empty household members if successful, or 404 if the user has no household
   */
  @GetMapping("/members/empty")
  public ResponseEntity<?> getEmptyHouseholdMembers(Principal principal) {
    try {
      List<EmptyHouseholdMemberDto> members = householdService.getEmptyHouseholdMembers(principal.getName());
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
   * @param principal the Principal object representing the current user
   * @return ResponseEntity with the created empty household member if successful, or an error message if the
   * user is not found or doesn't have a household
   */
  @PostMapping("/members/empty")
  public ResponseEntity<?> addEmptyHouseholdMember(
      @Valid @RequestBody EmptyHouseholdMemberCreateDto requestDto,
      Principal principal) {
    try {
      EmptyHouseholdMemberDto member = householdService.addEmptyHouseholdMember(principal.getName(), requestDto);
      return ResponseEntity.ok(member);
    } catch (IllegalStateException e) {
      log.info("Add empty household member failed: {}", e.getMessage());
      if (e.getMessage().equals("User doesn't have a household")) {
        return ResponseEntity.notFound().build();
      }
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  // TODO: Add endpoint to remove a household member as admin
}
