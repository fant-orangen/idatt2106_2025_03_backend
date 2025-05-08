package stud.ntnu.backend.dto.map;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import stud.ntnu.backend.model.map.CrisisEvent;

/**
 * Data Transfer Object (DTO) for detailed crisis event information.
 * <p>
 * This DTO contains comprehensive information about a crisis event, including its
 * geographical location, temporal data, severity level, and associated scenario theme.
 * It is used to transfer detailed crisis event data between layers of the application.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrisisEventDetailsDto {

    /**
     * The unique identifier of the crisis event.
     */
    private Integer id;

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
    private CrisisEvent.Severity severity;

    /**
     * The latitude coordinate of the crisis event's epicenter.
     */
    private BigDecimal epicenterLatitude;

    /**
     * The longitude coordinate of the crisis event's epicenter.
     */
    private BigDecimal epicenterLongitude;

    /**
     * The radius of the affected area in meters.
     */
    private BigDecimal radius;

    /**
     * The date and time when the crisis event started.
     */
    private LocalDateTime startTime;

    /**
     * The date and time when the crisis event was last updated.
     */
    private LocalDateTime updatedAt;

    /**
     * Indicates whether the crisis event is currently active.
     */
    private Boolean active;

    /**
     * The ID of the associated scenario theme, if any.
     */
    private Integer scenarioThemeId;

    /**
     * Converts a CrisisEvent entity to a CrisisEventDetailsDto.
     * <p>
     * This method maps all relevant fields from the entity to the DTO,
     * including the associated scenario theme ID if present.
     *
     * @param event the crisis event entity to convert
     * @return a new CrisisEventDetailsDto containing the entity's data
     */
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