package stud.ntnu.backend.dto.user;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for updating a user's profile information.
 * This class encapsulates the fields that can be modified in a user's profile,
 * including personal information and location data.
 */
@Setter
@Getter
public class UserUpdateDto {

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
     * Default constructor for UserUpdateDto.
     * Creates a new instance with default values.
     */
    public UserUpdateDto() {
    }

    /**
     * Constructor for UserUpdateDto with all fields.
     *
     * @param firstName     The user's first name
     * @param lastName      The user's last name
     * @param homeAddress   The user's home address
     * @param homeLatitude  The latitude coordinate of the user's home
     * @param homeLongitude The longitude coordinate of the user's home
     */
    public UserUpdateDto(String firstName, String lastName, String homeAddress,
            BigDecimal homeLatitude, BigDecimal homeLongitude) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.homeAddress = homeAddress;
        this.homeLatitude = homeLatitude;
        this.homeLongitude = homeLongitude;
    }
}