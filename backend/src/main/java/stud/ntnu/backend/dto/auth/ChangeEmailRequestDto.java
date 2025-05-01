package stud.ntnu.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for change email requests.
 */

@Getter
@Setter
public class ChangeEmailRequestDto {

    @NotBlank(message = "Email is required")
    private String email;
    @NotBlank(message = "New email is required")
    private String newEmail;
    @NotBlank(message = "Password is required")
    private String password;
}
