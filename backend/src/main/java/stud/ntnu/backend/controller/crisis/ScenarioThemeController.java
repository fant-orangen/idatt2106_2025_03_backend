package stud.ntnu.backend.controller.crisis;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
 * REST controller for managing crisis scenario themes. Provides endpoints for creating, updating,
 * and retrieving scenario themes like power outages or floods, along with their associated
 * educational and preparedness content.
 * <p>
 * This controller supports both public access for retrieving theme information and administrative
 * access for creating and updating themes.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Scenario Themes", description = "Operations related to crisis scenario theme management")
public class ScenarioThemeController {

  private final ScenarioThemeService scenarioThemeService;
  private final UserService userService;

  /**
   * Constructs a new ScenarioThemeController with the required services.
   *
   * @param scenarioThemeService service for managing scenario themes
   * @param userService          service for managing users
   */
  public ScenarioThemeController(ScenarioThemeService scenarioThemeService,
      UserService userService) {
    this.scenarioThemeService = scenarioThemeService;
    this.userService = userService;
  }

  /**
   * Creates a new scenario theme. Only users with ADMIN or SUPERADMIN roles are allowed.
   *
   * @param createScenarioThemeDto the scenario theme information including name, description, and
   *                               optional instructions
   * @param principal              the Principal object representing the current user
   * @return ResponseEntity with: - 200 OK and the created scenario theme if successful - 403
   * Forbidden if unauthorized - 400 Bad Request with error message if creation fails
   */
  @Operation(summary = "Create scenario theme", description = "Creates a new scenario theme. Only users with ADMIN or SUPERADMIN roles are allowed.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Scenario theme created successfully", 
          content = @Content(schema = @Schema(implementation = ScenarioTheme.class))),
      @ApiResponse(responseCode = "403", description = "Access forbidden - only administrators can create scenario themes", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid scenario theme data", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
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
   * @param updateScenarioThemeDto the scenario theme update information including id and optional
   *                               name, description, and instructions
   * @param principal              the Principal object representing the current user
   * @return ResponseEntity with: - 200 OK and the updated scenario theme if successful - 403
   * Forbidden if unauthorized - 400 Bad Request with error message if update fails
   */
  @Operation(summary = "Update scenario theme", description = "Updates an existing scenario theme. Only users with ADMIN or SUPERADMIN roles are allowed.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Scenario theme updated successfully", 
          content = @Content(schema = @Schema(implementation = ScenarioTheme.class))),
      @ApiResponse(responseCode = "403", description = "Access forbidden - only administrators can update scenario themes", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid update data", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
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
  @Operation(summary = "Get all scenario themes", description = "Gets a paginated list of all scenario themes.")
  @ApiResponse(responseCode = "200", description = "Successfully retrieved scenario themes", 
      content = @Content(schema = @Schema(implementation = ScenarioTheme.class)))
  @GetMapping("/public/scenario-themes/all")
  public ResponseEntity<Page<ScenarioTheme>> getAllScenarioThemes(Pageable pageable) {
    Page<ScenarioTheme> scenarioThemes = scenarioThemeService.getAllScenarioThemes(pageable);
    return ResponseEntity.ok(scenarioThemes);
  }

  /**
   * Gets a list of all scenario themes with just their names and IDs. This is a lightweight
   * endpoint for UI components that only need basic theme information.
   *
   * @return ResponseEntity containing a list of ScenarioThemeNameDto objects
   */
  @Operation(summary = "Get all scenario theme previews", description = "Gets a list of all scenario themes with just their names and IDs. This is a lightweight endpoint for UI components that only need basic theme information.")
  @ApiResponse(responseCode = "200", description = "Successfully retrieved scenario theme previews", 
      content = @Content(schema = @Schema(implementation = ScenarioThemeNameDto.class)))
  @GetMapping("/public/scenario-themes/previews/all")
  public ResponseEntity<List<ScenarioThemeNameDto>> getAllScenarioThemePreviews() {
    List<ScenarioThemeNameDto> previews = scenarioThemeService.getAllScenarioThemePreviews();
    return ResponseEntity.ok(previews);
  }

  /**
   * Gets detailed information about a specific scenario theme by its ID.
   *
   * @param id the ID of the scenario theme to retrieve
   * @return ResponseEntity with: - 200 OK and ScenarioThemeDetailsDto if found - 404 Not Found if
   * the theme doesn't exist
   */
  @Operation(summary = "Get scenario theme by ID", description = "Gets detailed information about a specific scenario theme by its ID.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved scenario theme details", 
          content = @Content(schema = @Schema(implementation = ScenarioThemeDetailsDto.class))),
      @ApiResponse(responseCode = "404", description = "Scenario theme not found")
  })
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
   * @return ResponseEntity with: - 200 OK and ScenarioThemeNameDto if found - 404 Not Found if the
   * theme doesn't exist
   */
  @Operation(summary = "Get scenario theme name", description = "Gets just the name of a specific scenario theme by its ID.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved scenario theme name", 
          content = @Content(schema = @Schema(implementation = ScenarioThemeNameDto.class))),
      @ApiResponse(responseCode = "404", description = "Scenario theme not found")
  })
  @GetMapping("/public/scenario-themes/{id}/name")
  public ResponseEntity<ScenarioThemeNameDto> getScenarioThemeName(@PathVariable Integer id) {
    return scenarioThemeService.getScenarioThemeNameById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }
}
