package stud.ntnu.backend.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.backend.dto.HouseholdCreateRequestDto;
import stud.ntnu.backend.model.Household;
import stud.ntnu.backend.service.HouseholdService;

/**
 * Handles household-level operations. Allows users to create or join households, modify population
 * count (including non-user members), switch households, and retrieve household-related data.
 * TODO: remove all logging which is not done properly
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
}
