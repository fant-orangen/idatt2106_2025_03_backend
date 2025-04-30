package stud.ntnu.backend.service;

import org.springframework.stereotype.Service;
import stud.ntnu.backend.repository.map.ScenarioThemeRepository;
import stud.ntnu.backend.model.map.ScenarioTheme;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing scenario themes. Handles creation, retrieval, updating, and deletion of
 * scenario themes.
 */
@Service
public class ScenarioThemeService {

  private final ScenarioThemeRepository scenarioThemeRepository;

  /**
   * Constructor for dependency injection.
   *
   * @param scenarioThemeRepository repository for scenario theme operations
   */
  public ScenarioThemeService(ScenarioThemeRepository scenarioThemeRepository) {
    this.scenarioThemeRepository = scenarioThemeRepository;
  }

  /**
   * Retrieves all scenario themes.
   *
   * @return list of all scenario themes
   */
  public List<ScenarioTheme> getAllScenarioThemes() {
    return scenarioThemeRepository.findAll();
  }

  /**
   * Retrieves a scenario theme by its ID.
   *
   * @param id the ID of the scenario theme
   * @return an Optional containing the scenario theme if found
   */
  public Optional<ScenarioTheme> getScenarioThemeById(Integer id) {
    return scenarioThemeRepository.findById(id);
  }

  /**
   * Saves a scenario theme.
   *
   * @param scenarioTheme the scenario theme to save
   * @return the saved scenario theme
   */
  public ScenarioTheme saveScenarioTheme(ScenarioTheme scenarioTheme) {
    return scenarioThemeRepository.save(scenarioTheme);
  }

  /**
   * Deletes a scenario theme by its ID.
   *
   * @param id the ID of the scenario theme to delete
   */
  public void deleteScenarioTheme(Integer id) {
    scenarioThemeRepository.deleteById(id);
  }

  /**
   * Creates a new scenario theme.
   *
   * @param dto  the DTO containing name and description
   * @param user the user creating the scenario theme
   * @return the saved scenario theme
   */
  public ScenarioTheme createScenarioTheme(stud.ntnu.backend.dto.map.CreateScenarioThemeDto dto,
      stud.ntnu.backend.model.user.User user) {
    ScenarioTheme scenarioTheme = new ScenarioTheme();
    scenarioTheme.setName(dto.getName());
    scenarioTheme.setDescription(dto.getDescription());
    scenarioTheme.setInstructions(dto.getInstructions());
    scenarioTheme.setCreatedByUser(user);
    // createdAt and updatedAt are set by @PrePersist
    return scenarioThemeRepository.save(scenarioTheme);
  }

  /**
   * Updates a scenario theme by id. Only non-null fields in the DTO are updated.
   *
   * @param dto the DTO containing id and fields to update
   * @return the updated scenario theme
   * @throws IllegalStateException if the scenario theme is not found
   */
  public ScenarioTheme updateScenarioTheme(stud.ntnu.backend.dto.map.UpdateScenarioThemeDto dto) {
    ScenarioTheme scenarioTheme = scenarioThemeRepository.findById(dto.getId())
        .orElseThrow(() -> new IllegalStateException("Scenario theme not found"));
    if (dto.getName() != null) {
      scenarioTheme.setName(dto.getName());
    }
    if (dto.getDescription() != null) {
      scenarioTheme.setDescription(dto.getDescription());
    }
    if (dto.getInstructions() != null) {
      scenarioTheme.setInstructions(dto.getInstructions());
    }
    return scenarioThemeRepository.save(scenarioTheme);
  }
}