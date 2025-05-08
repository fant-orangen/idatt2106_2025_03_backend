package stud.ntnu.backend.dto.auth;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for authentication responses.
 * <p>
 * This DTO encapsulates the data returned after successful authentication, including:
 * <ul>
 *   <li>JWT token for subsequent authenticated requests</li>
 *   <li>User identification information (ID, email)</li>
 *   <li>User role and permissions</li>
 *   <li>Household association</li>
 *   <li>Two-factor authentication status</li>
 * </ul>
 */
@Getter
@Setter
public class AuthResponseDto {

    /**
     * The JWT token used for authentication.
     */
    private String token;

    /**
     * The unique identifier of the authenticated user.
     */
    private Integer userId;

    /**
     * The email address of the authenticated user.
     */
    private String email;

    /**
     * The role of the authenticated user (e.g., "USER", "ADMIN").
     */
    private String role;

    /**
     * The identifier of the household associated with the user, if any.
     */
    private Integer householdId;

    /**
     * Indicates whether the user has two-factor authentication enabled.
     */
    private Boolean isUsing2FA;

    /**
     * Default constructor required for JSON deserialization.
     */
    public AuthResponseDto() {
    }

    /**
     * Constructs a new AuthResponseDto with only the authentication token.
     *
     * @param token the JWT authentication token
     */
    public AuthResponseDto(String token) {
        this.token = token;
    }

    /**
     * Constructs a new AuthResponseDto with all user information.
     *
     * @param token the JWT authentication token
     * @param userId the unique identifier of the user
     * @param email the user's email address
     * @param role the user's role in the system
     * @param householdId the identifier of the user's household
     * @param isUsing2FA whether the user has 2FA enabled
     */
    public AuthResponseDto(String token, Integer userId, String email, String role,
            Integer householdId, Boolean isUsing2FA) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.householdId = householdId;
        this.isUsing2FA = isUsing2FA;
    }
}