package stud.ntnu.backend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object for authentication requests. Contains the email and password for user
 * login.
 */
@Setter
@Getter
public class AuthRequestDto {

  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  private String email;

  @Size(min = 8, max = 100, message = "Password must be at between 8 and 100 characters long")
  @Pattern(
          //TODO Change this back to the one below when done with dev
          //Regex for password validation during dev
          // Regex for password validation during production:
          //regexp = "^(?=.*[\\p{L}])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$",
          regexp = "^[A-Za-z0-9\\p{L}\\p{M}\\p{P}\\p{S}]+$",
          message = "invalid password format"
  )
  private String password;

  @NotBlank
  private String recaptchaToken;

  // Default constructor
  public AuthRequestDto() {
  }

  // Constructor with parameters
  public AuthRequestDto(String email, String password, String recaptchaToken) {
    this.email = email;
    this.password = password;
    this.recaptchaToken = recaptchaToken;
  }
}