package stud.ntnu.backend.dto.map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import stud.ntnu.backend.model.map.CrisisEvent;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrisisEventDetailsDto {

  private Integer id;
  private String name;
  private String description;
  private CrisisEvent.Severity severity;
  private BigDecimal epicenterLatitude;
  private BigDecimal epicenterLongitude;
  private BigDecimal radius;
  private LocalDateTime startTime;
  private LocalDateTime updatedAt;
  private Boolean active;
  private Integer scenarioThemeId;

  public static CrisisEventDetailsDto fromEntity(CrisisEvent event) {
    return new CrisisEventDetailsDto(
        event.getId(),
        event.getName(),
        event.getDescription(),
        event.getSeverity(),
        event.getEpicenterLatitude(),
        event.getEpicenterLongitude(),
        event.getRadius(),
        event.getStartTime(),
        event.getUpdatedAt(),
        event.getActive(),
        event.getScenarioTheme() != null ? event.getScenarioTheme().getId() : null
    );
  }
} 