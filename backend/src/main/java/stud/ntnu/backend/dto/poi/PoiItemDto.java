package stud.ntnu.backend.dto.poi;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import stud.ntnu.backend.model.map.PointOfInterest;

/**
 * Data Transfer Object (DTO) representing a Point of Interest item.
 * <p>
 * This DTO contains all the necessary information about a Point of Interest that needs to be
 * returned to the client, including its location, type, and metadata.
 */
@Getter
@Setter
public class PoiItemDto {

    /**
     * The unique identifier of the Point of Interest.
     */
    private Integer id;

    /**
     * The unique identifier of the POI type.
     */
    private Integer poiTypeId;

    /**
     * The name of the POI type.
     */
    private String poiTypeName;

    /**
     * The name of the Point of Interest.
     */
    private String name;

    /**
     * A detailed description of the Point of Interest.
     */
    private String description;

    /**
     * The latitude coordinate of the POI's location.
     */
    private BigDecimal latitude;

    /**
     * The longitude coordinate of the POI's location.
     */
    private BigDecimal longitude;

    /**
     * The physical address of the Point of Interest.
     */
    private String address;

    /**
     * The opening time of the POI.
     */
    private String openFrom;

    /**
     * The closing time of the POI.
     */
    private String openTo;

    /**
     * Contact information for the Point of Interest.
     */
    private String contactInfo;

    /**
     * The date and time when the POI was created.
     */
    private LocalDateTime createdAt;

    /**
     * The date and time when the POI was last updated.
     */
    private LocalDateTime updatedAt;

    /**
     * Converts a PointOfInterest entity to a PoiItemDto.
     * <p>
     * This method maps all relevant fields from the PointOfInterest entity to the DTO,
     * including nested information about the POI type.
     *
     * @param poi the PointOfInterest entity to convert
     * @return a new PoiItemDto containing the mapped data
     */
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
        dto.setOpenFrom(poi.getOpenFrom());
        dto.setOpenTo(poi.getOpenTo());
        dto.setContactInfo(poi.getContactInfo());
        dto.setCreatedAt(poi.getCreatedAt());
        dto.setUpdatedAt(poi.getUpdatedAt());

        return dto;
    }
}