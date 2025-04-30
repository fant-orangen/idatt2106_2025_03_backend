package stud.ntnu.backend.dto.map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stud.ntnu.backend.model.map.CrisisEventChange;

import java.time.LocalDateTime;

/**
 * DTO for returning crisis event change information.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CrisisEventChangeDto {
    private Integer id;
    private Integer crisisEventId;
    private String changeType;
    private String oldValue;
    private String newValue;
    private Integer createdByUserId;
    private String createdByUserName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Converts a CrisisEventChange entity to a CrisisEventChangeDto.
     *
     * @param change the crisis event change entity
     * @return the crisis event change DTO
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
