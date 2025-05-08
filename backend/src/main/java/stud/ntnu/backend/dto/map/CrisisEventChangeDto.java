package stud.ntnu.backend.dto.map;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stud.ntnu.backend.model.map.CrisisEventChange;

/**
 * Data Transfer Object (DTO) for crisis event change information.
 * <p>
 * This DTO represents the changes made to a crisis event, including what was changed,
 * who made the change, and when the change occurred. It is used to track the history
 * of modifications to crisis events in the system.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CrisisEventChangeDto {

    /**
     * The unique identifier for this change record.
     */
    private Integer id;

    /**
     * The ID of the crisis event that was modified.
     */
    private Integer crisisEventId;

    /**
     * The type of change that was made (e.g., UPDATE, DELETE).
     */
    private String changeType;

    /**
     * The previous value before the change.
     */
    private String oldValue;

    /**
     * The new value after the change.
     */
    private String newValue;

    /**
     * The ID of the user who made the change.
     */
    private Integer createdByUserId;

    /**
     * The name of the user who made the change.
     */
    private String createdByUserName;

    /**
     * The timestamp when the change was created.
     */
    private LocalDateTime createdAt;

    /**
     * The timestamp when the change record was last updated.
     */
    private LocalDateTime updatedAt;

    /**
     * Converts a CrisisEventChange entity to a CrisisEventChangeDto.
     * <p>
     * This method maps all relevant fields from the entity to the DTO,
     * including the associated crisis event ID and user information.
     *
     * @param change the crisis event change entity to convert
     * @return a new CrisisEventChangeDto containing the entity's data
     */
    public static CrisisEventChangeDto fromEntity(CrisisEventChange change) {
        return new CrisisEventChangeDto(
            change.getId(),
            change.getCrisisEvent().getId(),
            change.getChangeType().name(),
            change.getOldValue(),
            change.getNewValue(),
            change.getCreatedByUser().getId(),
            change.getCreatedByUser().getName(),
            change.getCreatedAt(),
            change.getUpdatedAt()
        );
    }
}
