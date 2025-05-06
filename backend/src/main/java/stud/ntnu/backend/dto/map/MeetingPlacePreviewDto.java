package stud.ntnu.backend.dto.map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import stud.ntnu.backend.model.map.MeetingPlace;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingPlacePreviewDto {
    private Integer id;
    private String name;

    public static MeetingPlacePreviewDto fromEntity(MeetingPlace meetingPlace) {
        return new MeetingPlacePreviewDto(
            meetingPlace.getId(),
            meetingPlace.getName()
        );
    }
} 