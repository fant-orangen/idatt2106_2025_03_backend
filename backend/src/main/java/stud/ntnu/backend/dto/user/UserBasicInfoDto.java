package stud.ntnu.backend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing basic user information.
 * This class encapsulates essential user details including personal information
 * and household association.
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBasicInfoDto {
    /**
     * The user's first name.
     */
    private String firstName;

    /**
     * The user's last name.
     */
    private String lastName;

    /**
     * The user's email address.
     */
    private String email;

    /**
     * The name of the household the user belongs to.
     */
    private String householdName;

    /**
     * Flag indicating whether the user's email has been verified.
     */
    private Boolean emailVerified;

    /**
     * Constructs a UserBasicInfoDto with basic user information.
     * This constructor sets emailVerified to false by default for backward compatibility.
     *
     * @param firstName     the user's first name
     * @param lastName      the user's last name
     * @param email         the user's email address
     * @param householdName the name of the user's household
     */
    public UserBasicInfoDto(String firstName, String lastName, String email, String householdName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.householdName = householdName;
        this.emailVerified = false;
    }
}
