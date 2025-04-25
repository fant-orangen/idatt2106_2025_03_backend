package stud.ntnu.backend.dto.poi;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import stud.ntnu.backend.model.map.PointOfInterest;

/**
 * DTO for returning a Point of Interest item.
 */
@Setter
@Getter
public class PoiItemDto {

  private Integer id;
  private Integer poiTypeId;
  private String poiTypeName;
  private String name;
  private String description;
  private BigDecimal latitude;
  private BigDecimal longitude;
  private String address;
  private String openingHours;
  private String contactInfo;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;


  // Constructor with all fields
  public static PoiItemDto fromEntity(PointOfInterest poi) {
    PoiItemDto dto = new PoiItemDto();
    dto.setId(poi.getId());
    dto.setPoiTypeId(poi.getPoiType().getId());
    dto.setPoiTypeName(poi.getPoiType().getName());
    dto.setName(poi.getName());
    dto.setDescription(poi.getDescription());
    dto.setLatitude(poi.getLatitude());
    dto.setLongitude(poi.getLongitude());
    dto.setAddress(poi.getAddress());
    dto.setOpeningHours(poi.getOpeningHours());
    dto.setContactInfo(poi.getContactInfo());
    dto.setCreatedAt(poi.getCreatedAt());
    dto.setUpdatedAt(poi.getUpdatedAt());

    return dto;

  }
}