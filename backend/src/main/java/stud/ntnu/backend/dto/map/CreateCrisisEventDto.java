package stud.ntnu.backend.dto.map;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stud.ntnu.backend.model.map.CrisisEvent.Severity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) for creating a new crisis event.
 * <p>
 * This DTO contains all necessary information to create a new crisis event in the system, including
 * geographical coordinates, severity level, and temporal information. Some fields are optional
 * while others are required as indicated by the validation annotations.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCrisisEventDto {

  /**
   * The latitude coordinate of the crisis event location. This field is required.
   */
  @NotNull(message = "Latitude is required")
  private BigDecimal latitude;

  /**
   * The longitude coordinate of the crisis event location. This field is required.
   */
  @NotNull(message = "Longitude is required")
  private BigDecimal longitude;

  /**
   * The physical address of the crisis event. This field is optional.
   */
  private String address;

  /**
   * The radius of the affected area in meters. This field is optional.
   */
  private BigDecimal radius;

  /**
   * The severity level of the crisis event. This field is required.
   */
  @NotNull(message = "Severity is required")
  private Severity severity;

  /**
   * A detailed description of the crisis event. This field is optional.
   */
  private String description;

  /**
   * The name or title of the crisis event. This field is required.
   */
  @NotNull(message = "Name is required")
  private String name;

  /**
   * The date and time when the crisis event started. This field is required.
   */
  @NotNull(message = "Start time is required")
  private LocalDateTime startTime;

  /**
   * The ID of the associated scenario theme, if applicable. This field is optional.
   */
  private Integer scenarioThemeId;
}