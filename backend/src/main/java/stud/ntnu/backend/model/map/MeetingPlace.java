package stud.ntnu.backend.model.map;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import stud.ntnu.backend.model.user.User;

/**
 * Represents a meeting place in the system.
 * This entity stores information about physical locations where users can meet,
 * including geographical coordinates and metadata.
 */
@Entity
@Table(name = "meeting_places")
@Getter
@Setter
@NoArgsConstructor
public class MeetingPlace {

    /**
     * Unique identifier for the meeting place.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Name of the meeting place.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Latitude coordinate of the meeting place.
     * Stored with precision of 10 digits and 7 decimal places.
     */
    @Column(name = "latitude", nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;

    /**
     * Longitude coordinate of the meeting place.
     * Stored with precision of 10 digits and 7 decimal places.
     */
    @Column(name = "longitude", nullable = false, precision = 10, scale = 7)
    private BigDecimal longitude;

    /**
     * Physical address of the meeting place.
     */
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    /**
     * Current status of the meeting place.
     * Default value is "active".
     */
    @Column(name = "status", nullable = false)
    private String status = "active";

    /**
     * User who created this meeting place.
     */
    @ManyToOne
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdByUser;

    /**
     * Timestamp when the meeting place was created.
     * This field cannot be updated after creation.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Sets the creation timestamp before persisting the entity.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * Constructs a new meeting place with the specified parameters.
     *
     * @param name The name of the meeting place
     * @param latitude The latitude coordinate
     * @param longitude The longitude coordinate
     * @param createdByUser The user creating this meeting place
     */
    public MeetingPlace(String name, BigDecimal latitude, BigDecimal longitude, User createdByUser) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdByUser = createdByUser;
    }
}
