package stud.ntnu.backend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for handling forgot password requests.
 * This class encapsulates the email address of a user who has requested a password reset.
 * The email field is validated to ensure it is not blank and follows a valid email format.
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordRequestDto {

    /**
     * The email address of the user requesting a password reset.
     * Must not be blank and must be in a valid email format.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid format")
    private String email;
}