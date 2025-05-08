package stud.ntnu.backend.dto.map;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import stud.ntnu.backend.model.map.CrisisEvent;

/**
 * Data Transfer Object (DTO) for preview information of a crisis event.
 * <p>
 * This DTO contains essential information about a crisis event that is typically
 * displayed in preview or list views. It includes basic details such as the event's
 * identifier, name, severity level, and start time.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrisisEventPreviewDto {

    /**
     * The unique identifier of the crisis event.
     */
    private Integer id;

    /**
     * The name or title of the crisis event.
     */
    private String name;

    /**
     * The severity level of the crisis event.
     */
    private CrisisEvent.Severity severity;

    /**
     * The date and time when the crisis event started.
     */
    private LocalDateTime startTime;

    /**
     * Converts a CrisisEvent entity to a CrisisEventPreviewDto.
     * <p>
     * This method maps the essential fields from the entity to the DTO,
     * creating a simplified view of the crisis event.
     *
     * @param event the crisis event entity to convert
     * @return a new CrisisEventPreviewDto containing the entity's basic data
     */
    public static CrisisEventPreviewDto fromEntity(CrisisEvent event) {
        return new CrisisEventPreviewDto(
            event.getId(),
            event.getName(),
            event.getSeverity(),
            event.getStartTime()
        );
    }
}