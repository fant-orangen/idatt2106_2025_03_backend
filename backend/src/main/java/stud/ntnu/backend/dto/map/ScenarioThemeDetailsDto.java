package stud.ntnu.backend.dto.map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) representing the details of a scenario theme. This class is used to
 * transfer scenario theme information between layers of the application.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioThemeDetailsDto {

  /**
   * The name of the scenario theme.
   */
  private String name;

  /**
   * A detailed description of the scenario theme.
   */
  private String description;

  /**
   * Description of the state or condition before the scenario.
   */
  private String before;

  /**
   * Description of the state or condition during the scenario.
   */
  private String under;

  /**
   * Description of the state or condition after the scenario.
   */
  private String after;
}