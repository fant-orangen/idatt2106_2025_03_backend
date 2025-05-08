package stud.ntnu.backend.dto.map;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stud.ntnu.backend.model.map.CrisisEvent.Severity;

/**
 * Data Transfer Object (DTO) for updating an existing crisis event.
 * <p>
 * This DTO contains optional fields for updating a crisis event. If a field is null,
 * the corresponding field in the existing crisis event will not be updated.
 * <p>
 * When scenarioThemeId is provided, the system will attempt to update the scenario theme
 * if the ID exists. If the ID does not exist, a 404 error will be returned.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCrisisEventDto {

    /**
     * The name or title of the crisis event.
     */
    private String name;

    /**
     * A detailed description of the crisis event.
     */
    private String description;

    /**
     * The severity level of the crisis event.
     */
    private Severity severity;

    /**
     * The latitude coordinate of the crisis event location.
     * Represents the north-south position on Earth.
     */
    private BigDecimal latitude;

    /**
     * The longitude coordinate of the crisis event location.
     * Represents the east-west position on Earth.
     */
    private BigDecimal longitude;

    /**
     * The radius of the affected area in meters.
     */
    private BigDecimal radius;

    /**
     * The unique identifier of the associated scenario theme.
     * If provided, the system will attempt to update the scenario theme
     * if the ID exists.
     */
    private Integer scenarioThemeId;
}
