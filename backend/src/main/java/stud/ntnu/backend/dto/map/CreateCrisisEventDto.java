package stud.ntnu.backend.dto.map;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stud.ntnu.backend.model.map.CrisisEvent.Severity;

/**
 * DTO for creating a new crisis event.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCrisisEventDto {

  @NotNull(message = "Latitude is required")
  private BigDecimal latitude;

  @NotNull(message = "Longitude is required")
  private BigDecimal longitude;

  private String address; // Optional

  private BigDecimal radius;

  @NotNull(message = "Severity is required")
  private Severity severity;

  private String description;

  @NotNull(message = "Name is required")
  private String name;

  @NotNull(message = "Start time is required")
  private LocalDateTime startTime;
}