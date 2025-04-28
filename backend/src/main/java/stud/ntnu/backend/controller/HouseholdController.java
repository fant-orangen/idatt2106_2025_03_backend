package stud.ntnu.backend.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.backend.dto.household.HouseholdCreateRequestDto;
import stud.ntnu.backend.dto.household.HouseholdDto;
import stud.ntnu.backend.dto.household.HouseholdInviteRequestDto;
import stud.ntnu.backend.dto.household.HouseholdInviteResponseDto;
import stud.ntnu.backend.dto.household.HouseholdJoinRequestDto;
import stud.ntnu.backend.dto.household.HouseholdSwitchRequestDto;
import stud.ntnu.backend.model.household.Household;
import stud.ntnu.backend.service.HouseholdService;

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
   * @return ResponseEntity with the updated household if successful, or an error message if the
   * user or household is not found
   */
  @PutMapping("/switch")
  public ResponseEntity<?> switchHousehold(@Valid @RequestBody HouseholdSwitchRequestDto requestDto) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      String email = authentication.getName();

      Household household = householdService.switchHousehold(email, requestDto.getHouseholdId());
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
   * @return ResponseEntity with the invitation token if successful, or an error message if the
   * inviter or invitee is not found, or if the inviter doesn't have a household
   */
  @PostMapping("/invite")
  public ResponseEntity<?> inviteToHousehold(@Valid @RequestBody HouseholdInviteRequestDto requestDto) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      String email = authentication.getName();

      HouseholdInviteResponseDto response = householdService.inviteToHousehold(email, requestDto.getEmail());
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
   * @return ResponseEntity with the joined household if successful, or an error message if the
   * user is not found, the token is invalid or expired, or the household is not found
   */
  @PostMapping("/join")
  public ResponseEntity<?> joinHousehold(@Valid @RequestBody HouseholdJoinRequestDto requestDto) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      String email = authentication.getName();

      Household household = householdService.joinHousehold(email, requestDto.getToken());
      return ResponseEntity.ok(household);
    } catch (IllegalStateException e) {
      log.info("Household join failed: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Gets the authenticated user's household.
   *
   * @return ResponseEntity with the household if successful, or 404 if the user has no household
   */
  @GetMapping("/me")
  public ResponseEntity<?> getCurrentUserHousehold() {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      String email = authentication.getName();

      HouseholdDto household = householdService.getCurrentUserHousehold(email);
      return ResponseEntity.ok(household);
    } catch (IllegalStateException e) {
      log.info("Get household failed: {}", e.getMessage());
      if (e.getMessage().equals("User doesn't have a household")) {
        return ResponseEntity.notFound().build();
      }
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}
