package stud.ntnu.backend.service.crisis;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import stud.ntnu.backend.dto.map.CreateScenarioThemeDto;
import stud.ntnu.backend.dto.map.ScenarioThemeDetailsDto;
import stud.ntnu.backend.dto.map.ScenarioThemeNameDto;
import stud.ntnu.backend.dto.map.UpdateScenarioThemeDto;
import stud.ntnu.backend.model.map.ScenarioTheme;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.repository.map.ScenarioThemeRepository;

/**
 * Service for managing scenario themes. Handles creation, retrieval, updating, and deletion of
 * scenario themes.
 */
@Service
@RequiredArgsConstructor
public class ScenarioThemeService {

  private final ScenarioThemeRepository scenarioThemeRepository;

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
  public ScenarioTheme createScenarioTheme(CreateScenarioThemeDto dto, User user) {
    ScenarioTheme scenarioTheme = new ScenarioTheme();
    scenarioTheme.setName(dto.getName());
    scenarioTheme.setDescription(dto.getDescription());
    scenarioTheme.setBefore(dto.getBefore());
    scenarioTheme.setUnder(dto.getUnder());
    scenarioTheme.setAfter(dto.getAfter());
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
  public ScenarioTheme updateScenarioTheme(UpdateScenarioThemeDto dto) {
    ScenarioTheme scenarioTheme = scenarioThemeRepository.findById(dto.getId())
        .orElseThrow(() -> new IllegalStateException("Scenario theme not found"));
    if (dto.getName() != null) {
      scenarioTheme.setName(dto.getName());
    }
    if (dto.getDescription() != null) {
      scenarioTheme.setDescription(dto.getDescription());
    }
    if (dto.getBefore() != null) {
      scenarioTheme.setBefore(dto.getBefore());
    }
    if (dto.getUnder() != null) {
      scenarioTheme.setUnder(dto.getUnder());
    }
    if (dto.getAfter() != null) {
      scenarioTheme.setAfter(dto.getAfter());
    }
    if (dto.getStatus() != null) {
      scenarioTheme.setStatus(dto.getStatus());
    }
    return scenarioThemeRepository.save(scenarioTheme);
  }

  /**
   * Returns a paginated list of all scenario themes.
   *
   * @param pageable the pagination information
   * @return a page of scenario themes
   */
  public Page<ScenarioTheme> getAllScenarioThemes(Pageable pageable) {
    return scenarioThemeRepository.findAll(pageable);
  }

  /**
   * Gets scenario theme details (name, description, instructions) by id.
   *
   * @param id the scenario theme id
   * @return Optional containing ScenarioThemeDetailsDto if found
   */
  public Optional<ScenarioThemeDetailsDto> getScenarioThemeDetailsById(Integer id) {
    return scenarioThemeRepository.findById(id)
        .map(theme -> new ScenarioThemeDetailsDto(
            theme.getName(),
            theme.getDescription(),
            theme.getBefore(),
            theme.getUnder(),
            theme.getAfter()));
  }

  /**
   * Gets scenario theme name by id.
   *
   * @param id the scenario theme id
   * @return Optional containing ScenarioThemeNameDto if found
   */
  public Optional<ScenarioThemeNameDto> getScenarioThemeNameById(Integer id) {
    return scenarioThemeRepository.findById(id)
        .map(theme -> new ScenarioThemeNameDto(theme.getId(), theme.getName()));
  }

  /**
   * Gets a list of all scenario themes with just their IDs and names. This is a lightweight
   * endpoint for UI components that only need basic theme information.
   *
   * @return List of ScenarioThemeNameDto containing only IDs and names
   */
  public List<ScenarioThemeNameDto> getAllScenarioThemePreviews() {
    return scenarioThemeRepository.findAll().stream()
        .map(theme -> new ScenarioThemeNameDto(theme.getId(), theme.getName()))
        .toList();
  }
}