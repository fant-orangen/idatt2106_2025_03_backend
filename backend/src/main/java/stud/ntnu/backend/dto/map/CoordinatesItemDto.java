package stud.ntnu.backend.dto.map;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


/**
 * DTO for returning a coordinates item.
 */
@Getter
@Setter
public class CoordinatesItemDto {
    private BigDecimal latitude;
    private BigDecimal longitude;

    // Default constructor
    public CoordinatesItemDto() {
    }
    // Constructor with all fields
    public CoordinatesItemDto(String latitude, String longitude) {
        this.latitude = new BigDecimal(latitude);
        this.longitude = new BigDecimal(longitude);
    }
}

