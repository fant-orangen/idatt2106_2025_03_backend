package stud.ntnu.backend.dto.user;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) representing a user's complete profile information.
 * This class encapsulates all essential user details including personal information,
 * location data, and household association.
 */
@Setter
@Getter
public class UserProfileDto {

    /**
     * Unique identifier for the user.
     */
    private Integer id;

    /**
     * The user's email address.
     */
    private String email;

    /**
     * The user's first name.
     */
    private String firstName;

    /**
     * The user's last name.
     */
    private String lastName;

    /**
     * The user's home address.
     */
    private String homeAddress;

    /**
     * The latitude coordinate of the user's home location.
     */
    private BigDecimal homeLatitude;

    /**
     * The longitude coordinate of the user's home location.
     */
    private BigDecimal homeLongitude;

    /**
     * Flag indicating whether the user has enabled location sharing.
     */
    private Boolean locationSharingEnabled;

    /**
     * Flag indicating whether the user's email has been verified.
     */
    private Boolean emailVerified;

    /**
     * Unique identifier for the household the user belongs to.
     */
    private Integer householdId;

    /**
     * The name of the household the user belongs to.
     */
    private String householdName;

    /**
     * Default constructor for UserProfileDto.
     * Creates a new instance with default values.
     */
    public UserProfileDto() {
    }

    /**
     * Constructor for UserProfileDto with all fields.
     *
     * @param id                    The unique identifier for the user
     * @param email                 The user's email address
     * @param firstName            The user's first name
     * @param lastName             The user's last name
     * @param homeAddress          The user's home address
     * @param homeLatitude         The latitude coordinate of the user's home
     * @param homeLongitude        The longitude coordinate of the user's home
     * @param locationSharingEnabled Whether location sharing is enabled
     * @param emailVerified        Whether the user's email is verified
     * @param householdId          The unique identifier of the user's household
     * @param householdName        The name of the user's household
     */
    public UserProfileDto(Integer id, String email, String firstName, String lastName,
            String homeAddress, BigDecimal homeLatitude, BigDecimal homeLongitude,
            Boolean locationSharingEnabled, Boolean emailVerified,
            Integer householdId, String householdName) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.homeAddress = homeAddress;
        this.homeLatitude = homeLatitude;
        this.homeLongitude = homeLongitude;
        this.locationSharingEnabled = locationSharingEnabled;
        this.emailVerified = emailVerified;
        this.householdId = householdId;
        this.householdName = householdName;
    }
}