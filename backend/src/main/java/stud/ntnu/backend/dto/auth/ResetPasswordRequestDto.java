package stud.ntnu.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for handling password reset requests. This class encapsulates the data
 * required to reset a user's password, including the reset token and the new password.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequestDto {

  /**
   * The reset token received in the password reset link. This token is used to validate the
   * password reset request.
   */
  @NotBlank(message = "Token is required")
  private String token;

  /**
   * The new password to be set for the user. Must be between 8 and 100 characters long and contain
   * only valid characters. Valid characters include letters, numbers, and special characters.
   */
  @Size(min = 8, max = 100, message = "Password must be at between 8 and 100 characters long")
  @Pattern(
      regexp = "^(?=.*[\\p{L}])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$",
      message = "Invalid password format"
  )
  private String newPassword;
}