package stud.ntnu.backend.dto.poi;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for creating a new Point of Interest (POI).
 * <p>
 * This DTO contains all the necessary information required to create a new POI,
 * including its location, type, and optional details like operating hours and contact information.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePoiDto {

    /**
     * The name of the point of interest.
     * This field is required and cannot be null.
     */
    @NotNull(message = "Name is required")
    private String name;

    /**
     * The latitude coordinate of the POI's location.
     * This field is required and cannot be null.
     */
    @NotNull(message = "Latitude is required")
    private BigDecimal latitude;

    /**
     * The longitude coordinate of the POI's location.
     * This field is required and cannot be null.
     */
    @NotNull(message = "Longitude is required")
    private BigDecimal longitude;

    /**
     * The physical address of the point of interest.
     * This field is optional.
     */
    private String address;

    /**
     * The unique identifier of the POI type.
     * This field is required and cannot be null.
     */
    @NotNull(message = "POI type is required")
    private Integer poiTypeId;

    /**
     * A detailed description of the point of interest.
     * This field is optional.
     */
    private String description;

    /**
     * The opening time of the POI.
     * This field is optional.
     */
    private String openFrom;

    /**
     * The closing time of the POI.
     * This field is optional.
     */
    private String openTo;

    /**
     * Contact information for the point of interest.
     * This field is optional.
     */
    private String contactInfo;
}
