package stud.ntnu.backend.repository.map;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import stud.ntnu.backend.model.map.PoiType;
import stud.ntnu.backend.model.map.PointOfInterest;

/**
 * Repository interface for managing PointOfInterest entities in the database.
 * Provides methods for basic CRUD operations and custom queries for point of interest data.
 */
@Repository
public interface PointOfInterestRepository extends JpaRepository<PointOfInterest, Integer> {

    /**
     * Retrieves all points of interest associated with a specific POI type ID.
     *
     * @param poiTypeId the ID of the POI type to search for
     * @return a list of points of interest matching the specified POI type ID
     */
    List<PointOfInterest> findByPoiTypeId(Integer poiTypeId);

    /**
     * Updates an existing point of interest with new information.
     * This method performs a direct database update using a custom query.
     *
     * @param id the ID of the point of interest to update
     * @param name the new name for the point of interest
     * @param latitude the new latitude coordinate
     * @param longitude the new longitude coordinate
     * @param description the new description
     * @param openFrom the new opening time
     * @param openTo the new closing time
     * @param contactInfo the new contact information
     * @param poiType the new POI type
     */
    @Modifying
    @Query("UPDATE PointOfInterest p SET p.name = :name, p.latitude = :latitude, " +
            "p.longitude = :longitude, p.description = :description, " +
            "p.openFrom = :openFrom, p.openTo = :openTo, p.contactInfo = :contactInfo, " +
            "p.poiType = :poiType WHERE p.id = :id")
    void updatePointOfInterest(
            @Param("id") Integer id,
            @Param("name") String name,
            @Param("latitude") BigDecimal latitude,
            @Param("longitude") BigDecimal longitude,
            @Param("description") String description,
            @Param("openFrom") String openFrom,
            @Param("openTo") String openTo,
            @Param("contactInfo") String contactInfo,
            @Param("poiType") PoiType poiType
    );
}