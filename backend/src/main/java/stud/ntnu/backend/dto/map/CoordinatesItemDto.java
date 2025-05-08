package stud.ntnu.backend.dto.map;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) representing geographical coordinates.
 * This class is used to transfer latitude and longitude data between layers of the application.
 */
@Getter
@Setter
public class CoordinatesItemDto {

    /**
     * The latitude coordinate value.
     */
    private BigDecimal latitude;

    /**
     * The longitude coordinate value.
     */
    private BigDecimal longitude;

    /**
     * Default constructor for creating an empty coordinates item.
     */
    public CoordinatesItemDto() {
    }

    /**
     * Constructs a new CoordinatesItemDto with the specified latitude and longitude values.
     *
     * @param latitude  The latitude value as a string
     * @param longitude The longitude value as a string
     */
    public CoordinatesItemDto(String latitude, String longitude) {
        this.latitude = new BigDecimal(latitude);
        this.longitude = new BigDecimal(longitude);
    }
}
