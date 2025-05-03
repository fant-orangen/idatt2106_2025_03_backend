package stud.ntnu.backend.dto.map;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stud.ntnu.backend.model.map.MeetingPlace;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingPlaceDto {
    private String name;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String address;

    public static MeetingPlaceDto fromEntity(MeetingPlace meetingPlace) {
        return new MeetingPlaceDto(
            meetingPlace.getName(),
            meetingPlace.getLatitude(),
            meetingPlace.getLongitude(),
            meetingPlace.getAddress()
        );
    }
} 