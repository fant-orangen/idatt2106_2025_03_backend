package stud.ntnu.backend.dto.map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import stud.ntnu.backend.model.map.CrisisEvent;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrisisEventPreviewDto {

  private Integer id;
  private String name;
  private CrisisEvent.Severity severity;
  private LocalDateTime startTime;

  public static CrisisEventPreviewDto fromEntity(CrisisEvent event) {
    return new CrisisEventPreviewDto(
        event.getId(),
        event.getName(),
        event.getSeverity(),
        event.getStartTime()
    );
  }
} 