package stud.ntnu.backend.dto.map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import stud.ntnu.backend.model.map.MeetingPlace;

/**
 * Data Transfer Object (DTO) for preview information of a meeting place.
 * <p>
 * This DTO contains essential information about a meeting place that is typically
 * displayed in preview or list views. It includes basic details such as the meeting place's
 * identifier, name, and current status.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingPlacePreviewDto {
    
    /**
     * The unique identifier of the meeting place.
     */
    private Integer id;
    
    /**
     * The name of the meeting place.
     */
    private String name;
    
    /**
     * The current status of the meeting place.
     */
    private String status;

    /**
     * Converts a MeetingPlace entity to a MeetingPlacePreviewDto.
     * <p>
     * This method maps the essential fields from the entity to the DTO,
     * creating a simplified view of the meeting place.
     *
     * @param meetingPlace the meeting place entity to convert
     * @return a new MeetingPlacePreviewDto containing the entity's basic data
     */
    public static MeetingPlacePreviewDto fromEntity(MeetingPlace meetingPlace) {
        return new MeetingPlacePreviewDto(
            meetingPlace.getId(),
            meetingPlace.getName(),
            meetingPlace.getStatus()
        );
    }
}