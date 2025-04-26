package stud.ntnu.backend.dto.map;

import java.math.BigDecimal;
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
  private BigDecimal latitude;
  private BigDecimal longitude;
  private String address; // Optional
  private BigDecimal radius;
  private Severity severity;
  private String description;
  private String name;
}