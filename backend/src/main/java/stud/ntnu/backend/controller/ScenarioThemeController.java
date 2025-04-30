package stud.ntnu.backend.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import stud.ntnu.backend.dto.map.CreateScenarioThemeDto;
import stud.ntnu.backend.dto.map.UpdateScenarioThemeDto;
import stud.ntnu.backend.model.map.ScenarioTheme;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.security.AdminChecker;
import stud.ntnu.backend.service.ScenarioThemeService;
import stud.ntnu.backend.service.UserService;

/**
 * Admin controller for managing crisis scenario themes. Supports CRUD operations for themes like
 * strømbrudd or flom, along with corresponding educational and preparedness content.
 * <p>
 * Based on Visjonsdokument 2025 for Krisefikser.no.
 */

@RestController
@RequestMapping("/api/scenario-themes")
public class ScenarioThemeController {

  private final ScenarioThemeService scenarioThemeService;
  private final UserService userService;

  public ScenarioThemeController(ScenarioThemeService scenarioThemeService,
      UserService userService) {
    this.scenarioThemeService = scenarioThemeService;
    this.userService = userService;
  }

  /**
   * Creates a new scenario theme. Only users with ADMIN or SUPERADMIN roles are allowed.
   * TODO: untested!
   * @param createScenarioThemeDto the scenario theme information (name, description, and optional instructions)
   * @param principal              the Principal object representing the current user
   * @return ResponseEntity with status 200 OK if successful, or 403 Forbidden if unauthorized
   */
  @PostMapping
  public ResponseEntity<?> createScenarioTheme(
      @Valid @RequestBody CreateScenarioThemeDto createScenarioThemeDto,
      Principal principal) {
    try {
      if (!AdminChecker.isCurrentUserAdmin(principal, userService)) {
        return ResponseEntity.status(403).body("Only administrators can create scenario themes");
      }
      String email = principal.getName();
      User currentUser = userService.getUserByEmail(email)
          .orElseThrow(() -> new IllegalStateException("User not found"));
      ScenarioTheme savedScenarioTheme = scenarioThemeService.createScenarioTheme(
          createScenarioThemeDto, currentUser);
      return ResponseEntity.ok(savedScenarioTheme);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Updates an existing scenario theme. Only users with ADMIN or SUPERADMIN roles are allowed.
   * TODO: untested!
   * @param updateScenarioThemeDto the scenario theme update information (id, and optionally name, description, instructions)
   * @param principal              the Principal object representing the current user
   * @return ResponseEntity with status 200 OK if successful, or 403 Forbidden if unauthorized
   */
  @PatchMapping
  public ResponseEntity<?> updateScenarioTheme(
          @Valid @RequestBody UpdateScenarioThemeDto updateScenarioThemeDto,
          Principal principal) {
    try {
      if (!AdminChecker.isCurrentUserAdmin(principal, userService)) {
        return ResponseEntity.status(403).body("Only administrators can update scenario themes");
      }
      ScenarioTheme updatedScenarioTheme = scenarioThemeService.updateScenarioTheme(updateScenarioThemeDto);
      return ResponseEntity.ok(updatedScenarioTheme);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}
