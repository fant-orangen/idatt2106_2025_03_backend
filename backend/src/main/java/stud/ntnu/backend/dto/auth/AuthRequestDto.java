package stud.ntnu.backend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for authentication requests.
 * <p>
 * This DTO encapsulates the data required for user authentication, including:
 * <ul>
 *   <li>Email address for user identification</li>
 *   <li>Password for authentication</li>
 *   <li>reCAPTCHA token for bot prevention</li>
 * </ul>
 * <p>
 * The class includes validation constraints to ensure data integrity and security.
 */
@Setter
@Getter
public class AuthRequestDto {

  /**
   * The user's email address. Must be a valid email format and cannot be blank.
   */
  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  private String email;

  /**
   * The user's password. Must be between 8 and 100 characters and contain only allowed characters.
   * Allowed characters include letters, numbers, and Unicode symbols.
   */
  @Size(min = 8, max = 100, message = "Password must be at between 8 and 100 characters long")
  @Pattern(
      regexp = "^[A-Za-z0-9\\p{L}\\p{M}\\p{P}\\p{S}]+$",
      message = "invalid password format"
  )
  private String password;

  /**
   * The reCAPTCHA verification token. Required for bot prevention and cannot be blank.
   */
  @NotBlank
  private String recaptchaToken;

  /**
   * Default constructor required for JSON deserialization.
   */
  public AuthRequestDto() {
  }

  /**
   * Constructs a new AuthRequestDto with the specified credentials.
   *
   * @param email          the user's email address
   * @param password       the user's password
   * @param recaptchaToken the reCAPTCHA verification token
   */
  public AuthRequestDto(String email, String password, String recaptchaToken) {
    this.email = email;
    this.password = password;
    this.recaptchaToken = recaptchaToken;
  }
}