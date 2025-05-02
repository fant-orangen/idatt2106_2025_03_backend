package stud.ntnu.backend.dto.map;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stud.ntnu.backend.model.map.CrisisEvent.Severity;

/**
 * DTO for updating an existing crisis event.
 * All fields are optional - if a field is null, it will not be updated.
 * If scenarioThemeId is provided, the scenario theme will be updated if the id exists; otherwise, a 404 will be returned.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCrisisEventDto {
  private String name;
  private String description;
  private Severity severity;
  private BigDecimal latitude;
  private BigDecimal longitude;
  private BigDecimal radius; // TODO: add optional scenario theme id
  private Integer scenarioThemeId;
}
