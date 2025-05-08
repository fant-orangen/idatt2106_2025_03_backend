package stud.ntnu.backend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for returning basic user information including first name, last name, email, household name, and email verification status.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBasicInfoDto {
    private String firstName;
    private String lastName;
    private String email;
    private String householdName;
    private Boolean emailVerified;

    /**
     * Constructor without emailVerified field for backward compatibility.
     *
     * @param firstName     the user's first name
     * @param lastName      the user's last name
     * @param email         the user's email
     * @param householdName the name of the user's household
     */
    public UserBasicInfoDto(String firstName, String lastName, String email, String householdName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.householdName = householdName;
        this.emailVerified = false; // Default value
    }
}
