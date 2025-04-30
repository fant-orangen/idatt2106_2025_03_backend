package stud.ntnu.backend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for handling forgot password requests.
 * Contains the email address of the user requesting a password reset.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordRequestDto {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
}