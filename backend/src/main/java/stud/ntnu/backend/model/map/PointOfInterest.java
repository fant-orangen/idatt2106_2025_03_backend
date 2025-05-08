package stud.ntnu.backend.model.map;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import stud.ntnu.backend.model.user.User;

/**
 * Represents a point of interest on a map with its associated metadata.
 * This entity stores information about locations that users can interact with,
 * including their geographical coordinates, type, and operational details.
 */
@Entity
@Table(name = "points_of_interest")
@Getter
@Setter
@NoArgsConstructor
public class PointOfInterest {

    /**
     * Unique identifier for the point of interest.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The type of point of interest (e.g., restaurant, museum, park).
     */
    @ManyToOne
    @JoinColumn(name = "poi_type_id", nullable = false)
    private PoiType poiType;

    /**
     * The name of the point of interest.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Detailed description of the point of interest.
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Latitude coordinate of the point of interest.
     */
    @Column(name = "latitude", nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;

    /**
     * Longitude coordinate of the point of interest.
     */
    @Column(name = "longitude", nullable = false, precision = 10, scale = 7)
    private BigDecimal longitude;

    /**
     * Physical address of the point of interest.
     */
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    /**
     * Opening hours start time.
     */
    @Column(name = "open_from", columnDefinition = "TEXT")
    private String openFrom;

    /**
     * Opening hours end time.
     */
    @Column(name = "open_to", columnDefinition = "TEXT")
    private String openTo;

    /**
     * Contact information for the point of interest.
     */
    @Column(name = "contact_info", columnDefinition = "TEXT")
    private String contactInfo;

    /**
     * User who created this point of interest.
     */
    @ManyToOne
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdByUser;

    /**
     * Timestamp when the point of interest was created.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the point of interest was last updated.
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Sets the creation and update timestamps before persisting the entity.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Updates the timestamp when the entity is modified.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Constructs a new PointOfInterest with the required fields.
     *
     * @param poiType The type of point of interest
     * @param name The name of the point of interest
     * @param latitude The latitude coordinate
     * @param longitude The longitude coordinate
     * @param createdByUser The user who created this point of interest
     */
    public PointOfInterest(PoiType poiType, String name, BigDecimal latitude, BigDecimal longitude,
            User createdByUser) {
        this.poiType = poiType;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdByUser = createdByUser;
    }
}
