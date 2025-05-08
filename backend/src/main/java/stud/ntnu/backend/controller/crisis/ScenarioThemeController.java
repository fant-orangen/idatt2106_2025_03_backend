package stud.ntnu.backend.controller.crisis;

import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import stud.ntnu.backend.dto.map.CreateScenarioThemeDto;
import stud.ntnu.backend.dto.map.ScenarioThemeDetailsDto;
import stud.ntnu.backend.dto.map.ScenarioThemeNameDto;
import stud.ntnu.backend.dto.map.UpdateScenarioThemeDto;
import stud.ntnu.backend.model.map.ScenarioTheme;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.security.AdminChecker;
import stud.ntnu.backend.service.crisis.ScenarioThemeService;
import stud.ntnu.backend.service.user.UserService;

/**
 * REST controller for managing crisis scenario themes.
 * Provides endpoints for creating, updating, and retrieving scenario themes like power outages
 * or floods, along with their associated educational and preparedness content.
 * <p>
 * This controller supports both public access for retrieving theme information and
 * administrative access for creating and updating themes.
 */
@RestController
@RequestMapping("/api")
public class ScenarioThemeController {

  private final ScenarioThemeService scenarioThemeService;
  private final UserService userService;

  /**
   * Constructs a new ScenarioThemeController with the required services.
   *
   * @param scenarioThemeService service for managing scenario themes
   * @param userService service for managing users
   */
  public ScenarioThemeController(ScenarioThemeService scenarioThemeService,
      UserService userService) {
    this.scenarioThemeService = scenarioThemeService;
    this.userService = userService;
  }

  /**
   * Creates a new scenario theme. Only users with ADMIN or SUPERADMIN roles are allowed.
   *
   * @param createScenarioThemeDto the scenario theme information including name, description,
   *                              and optional instructions
   * @param principal the Principal object representing the current user
   * @return ResponseEntity with:
   *         - 200 OK and the created scenario theme if successful
   *         - 403 Forbidden if unauthorized
   *         - 400 Bad Request with error message if creation fails
   */
  @PostMapping(path = "/admin/scenario-themes")
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
   * @param updateScenarioThemeDto the scenario theme update information including id and
   *                              optional name, description, and instructions
   * @param principal the Principal object representing the current user
   * @return ResponseEntity with:
   *         - 200 OK and the updated scenario theme if successful
   *         - 403 Forbidden if unauthorized
   *         - 400 Bad Request with error message if update fails
   */
  @PatchMapping(path = "/admin/scenario-themes")
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
   * Gets a paginated list of all scenario themes.
   *
   * @param pageable the pagination information including page number, size, and sorting
   * @return ResponseEntity with a page of scenario themes
   */
  @GetMapping("/public/scenario-themes/all")
  public ResponseEntity<Page<ScenarioTheme>> getAllScenarioThemes(Pageable pageable) {
    Page<ScenarioTheme> scenarioThemes = scenarioThemeService.getAllScenarioThemes(pageable);
    return ResponseEntity.ok(scenarioThemes);
  }

  /**
   * Gets a list of all scenario themes with just their names and IDs.
   * This is a lightweight endpoint for UI components that only need basic theme information.
   *
   * @return ResponseEntity containing a list of ScenarioThemeNameDto objects
   */
  @GetMapping("/public/scenario-themes/previews/all")
  public ResponseEntity<List<ScenarioThemeNameDto>> getAllScenarioThemePreviews() {
    List<ScenarioThemeNameDto> previews = scenarioThemeService.getAllScenarioThemePreviews();
    return ResponseEntity.ok(previews);
  }

  /**
   * Gets detailed information about a specific scenario theme by its ID.
   *
   * @param id the ID of the scenario theme to retrieve
   * @return ResponseEntity with:
   *         - 200 OK and ScenarioThemeDetailsDto if found
   *         - 404 Not Found if the theme doesn't exist
   */
  @GetMapping("/public/scenario-themes/{id}")
  public ResponseEntity<ScenarioThemeDetailsDto> getScenarioTheme(@PathVariable Integer id) {
    return scenarioThemeService.getScenarioThemeDetailsById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  /**
   * Gets just the name of a specific scenario theme by its ID.
   *
   * @param id the ID of the scenario theme to retrieve
   * @return ResponseEntity with:
   *         - 200 OK and ScenarioThemeNameDto if found
   *         - 404 Not Found if the theme doesn't exist
   */
  @GetMapping("/public/scenario-themes/{id}/name")
  public ResponseEntity<ScenarioThemeNameDto> getScenarioThemeName(@PathVariable Integer id) {
    return scenarioThemeService.getScenarioThemeNameById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }
}
