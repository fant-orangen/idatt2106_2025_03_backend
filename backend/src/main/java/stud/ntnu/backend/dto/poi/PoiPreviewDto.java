package stud.ntnu.backend.dto.poi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing a preview of a Point of Interest (POI). This class
 * contains basic information about a POI that can be used for preview purposes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PoiPreviewDto {

  /**
   * The unique identifier of the POI.
   */
  private Integer id;

  /**
   * The name of the POI.
   */
  private String name;

  /**
   * The type of the POI.
   */
  private String type;

  /**
   * Creates a PoiPreviewDto from a PoiItemDto.
   *
   * @param poi The PoiItemDto to convert
   * @return A new PoiPreviewDto containing the basic information from the PoiItemDto
   */
  public static PoiPreviewDto fromEntity(PoiItemDto poi) {
    return new PoiPreviewDto(
        poi.getId(),
        poi.getName(),
        poi.getPoiTypeName()
    );
  }
}
