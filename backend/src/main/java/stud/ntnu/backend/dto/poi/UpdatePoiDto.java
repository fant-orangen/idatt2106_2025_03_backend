package stud.ntnu.backend.dto.poi;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for updating an existing Point of Interest (POI). All fields are
 * optional - if a field is null, it will not be updated in the database. This DTO is used to
 * partially update POI information without requiring all fields to be provided.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePoiDto {

  /**
   * The name of the POI.
   */
  private String name;

  /**
   * The latitude coordinate of the POI.
   */
  private BigDecimal latitude;

  /**
   * The longitude coordinate of the POI.
   */
  private BigDecimal longitude;

  /**
   * A detailed description of the POI.
   */
  private String description;

  /**
   * The opening time of the POI in HH:mm format. Optional field.
   */
  private String openFrom;

  /**
   * The closing time of the POI in HH:mm format. Optional field.
   */
  private String openTo;

  /**
   * Contact information for the POI. Optional field.
   */
  private String contactInfo;

  /**
   * The ID of the POI type. Optional field, can be null if the type is not being updated.
   */
  private Integer poiTypeId;
}
