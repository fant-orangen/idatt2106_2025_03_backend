package stud.ntnu.backend.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import stud.ntnu.backend.dto.map.CreateScenarioThemeDto;
import stud.ntnu.backend.dto.map.UpdateScenarioThemeDto;
import stud.ntnu.backend.dto.map.ScenarioThemeDetailsDto;
import stud.ntnu.backend.dto.map.ScenarioThemeNameDto;
import stud.ntnu.backend.model.map.ScenarioTheme;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.security.AdminChecker;
import stud.ntnu.backend.service.ScenarioThemeService;
import stud.ntnu.backend.service.UserService;

/**
 * Admin controller for managing crisis scenario themes. Supports CRUD operations for themes like
 * strømbrudd or flom, along with corresponding educational and preparedness content.
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
   *
   * @param createScenarioThemeDto the scenario theme information (name, description, and optional
   *                               instructions)
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
   *
   * @param updateScenarioThemeDto the scenario theme update information (id, and optionally name,
   *                               description, instructions)
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
      ScenarioTheme updatedScenarioTheme = scenarioThemeService.updateScenarioTheme(
          updateScenarioThemeDto);
      return ResponseEntity.ok(updatedScenarioTheme);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Gets all scenario themes with pagination.
   *
   * @param pageable the pagination information
   * @return ResponseEntity with a page of scenario themes
   */
  @GetMapping("/all")
  public ResponseEntity<Page<ScenarioTheme>> getAllScenarioThemes(Pageable pageable) {
    Page<ScenarioTheme> scenarioThemes = scenarioThemeService.getAllScenarioThemes(pageable);
    return ResponseEntity.ok(scenarioThemes);
  }

  /**
   * Gets scenario theme details (name, description, instructions) by id.
   *
   * @param id the scenario theme id
   * @return ResponseEntity with ScenarioThemeDetailsDto or 404 if not found
   */
  @GetMapping("/{id}")
  public ResponseEntity<ScenarioThemeDetailsDto> getScenarioTheme(@PathVariable Integer id) {
    return scenarioThemeService.getScenarioThemeDetailsById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  /**
   * Gets scenario theme name by id.
   *
   * @param id the scenario theme id
   * @return ResponseEntity with ScenarioThemeNameDto or 404 if not found
   */
  @GetMapping("/{id}/name")
  public ResponseEntity<ScenarioThemeNameDto> getScenarioThemeName(@PathVariable Integer id) {
    return scenarioThemeService.getScenarioThemeNameById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }
}
