package stud.ntnu.backend.dto.poi;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
/**
 * DTO for updating an existing point of interest (POI).
 * All fields are optional - if a field is null, it will not be updated.
 */
public class UpdatePoiDto {
    private String name;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String description;
    private String openFrom; // Optional
    private String openTo; // Optional
    private String contactInfo; // Optional
    private Integer poiTypeId; // Optional, can be null if not updated
}
