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
}