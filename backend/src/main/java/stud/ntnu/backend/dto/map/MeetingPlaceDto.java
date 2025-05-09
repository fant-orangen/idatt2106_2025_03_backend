package stud.ntnu.backend.dto.map;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stud.ntnu.backend.model.map.MeetingPlace;

/**
 * Data Transfer Object (DTO) for meeting place information.
 * <p>
 * This DTO represents a meeting place in the system, including its geographical location, address,
 * and current status. It is used to transfer meeting place data between different layers of the
 * application.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingPlaceDto {

  /**
   * The unique identifier of the meeting place.
   */
  private Integer id;

  /**
   * The name of the meeting place.
   */
  private String name;

  /**
   * The latitude coordinate of the meeting place. Represents the north-south position on Earth.
   */
  private BigDecimal latitude;

  /**
   * The longitude coordinate of the meeting place. Represents the east-west position on Earth.
   */
  private BigDecimal longitude;

  /**
   * The physical address of the meeting place.
   */
  private String address;

  /**
   * The current status of the meeting place.
   */
  private String status;

  /**
   * Converts a MeetingPlace entity to a MeetingPlaceDto.
   * <p>
   * This method maps all relevant fields from the entity to the DTO, creating a data transfer
   * object that can be safely sent to clients.
   *
   * @param meetingPlace the meeting place entity to convert
   * @return a new MeetingPlaceDto containing the entity's data
   */
  public static MeetingPlaceDto fromEntity(MeetingPlace meetingPlace) {
    return new MeetingPlaceDto(
        meetingPlace.getId(),
        meetingPlace.getName(),
        meetingPlace.getLatitude(),
        meetingPlace.getLongitude(),
        meetingPlace.getAddress(),
        meetingPlace.getStatus()
    );
  }
}