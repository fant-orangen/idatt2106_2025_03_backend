package stud.ntnu.backend.repository.map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.map.PoiType;
import stud.ntnu.backend.model.map.PointOfInterest;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository interface for PointOfInterest entity operations.
 */
@Repository
public interface PointOfInterestRepository extends JpaRepository<PointOfInterest, Integer> {
    // Basic CRUD operations are provided by JpaRepository
    // Custom query methods can be added as needed
    List<PointOfInterest> findByPoiTypeId(Integer poiTypeId);



    // Update point of interest fields directly using a query
    @Modifying
    @Query("UPDATE PointOfInterest p SET p.name = :name, p.latitude = :latitude, " +
            "p.longitude = :longitude, p.description = :description, " +
            "p.openingHours = :openingHours, p.contactInfo = :contactInfo, " +
            "p.poiType = :poiType WHERE p.id = :id")
    void updatePointOfInterest(
            @Param("id") Integer id,
            @Param("name") String name,
            @Param("latitude") BigDecimal latitude,
            @Param("longitude") BigDecimal longitude,
            @Param("description") String description,
            @Param("openingHours") String openingHours,
            @Param("contactInfo") String contactInfo,
            @Param("poiType") PoiType poiType
    );
}