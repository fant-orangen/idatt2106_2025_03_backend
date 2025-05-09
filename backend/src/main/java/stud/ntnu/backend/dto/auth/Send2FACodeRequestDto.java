package stud.ntnu.backend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for handling two-factor authentication (2FA) code requests. This class
 * encapsulates the email address required to send a 2FA code to a user. The email field is
 * validated to ensure it is not blank and follows a valid email format.
 *
 * @author NTNU Backend Team
 * @version 1.0
 */
@Getter
@Setter
public class Send2FACodeRequestDto {

  /**
   * The email address of the user requesting a 2FA code. Must not be blank and must be a valid
   * email format.
   */
  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  private String email;
}