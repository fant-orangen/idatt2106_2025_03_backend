package stud.ntnu.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for handling password reset requests. Contains the token received in the reset link and the
 * new password.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequestDto {

  @NotBlank(message = "Token is required")
  private String token;

  @Size(min = 8, max = 100, message = "Password must be at between 8 and 100 characters long")
  @Pattern(
          regexp = "^[A-Za-z0-9\\p{L}\\p{M}\\p{P}\\p{S}]+$",
          message = "Invalid password format"
  )
  private String newPassword;
}