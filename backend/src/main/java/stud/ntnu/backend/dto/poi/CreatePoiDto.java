package stud.ntnu.backend.dto.poi;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO for creating a new point of interest (POI).
 * This class is currently empty, but can be extended in the future.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class CreatePoiDto {
    @NotNull(message = "Name is required")
    private String name;

    @NotNull(message = "Latitude is required")
    private BigDecimal latitude;

    @NotNull(message = "Longitude is required")
    private BigDecimal longitude;

    private String address; // Optional

    @NotNull(message = "POI type is required")
    private Integer poiTypeId;

    private String description; // Optional
    private String openFrom; // Optional
    private String openTo; // Optional
    private String contactInfo; // Optional


}
