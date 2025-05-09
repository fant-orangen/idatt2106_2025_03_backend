package stud.ntnu.backend.dto.auth;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for handling password change requests.
 * <p>
 * This DTO encapsulates the data required for changing a user's password, including:
 * <ul>
 *   <li>Current password for verification</li>
 *   <li>New password to be set</li>
 *   <li>Confirmation of the new password to prevent typos</li>
 * </ul>
 * <p>
 * The class includes validation constraints to ensure password security:
 * <ul>
 *   <li>Minimum length of 8 characters</li>
 *   <li>Maximum length of 100 characters</li>
 *   <li>Allowed characters include letters, numbers, and Unicode symbols</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordDto {

  /**
   * The user's current password.
   */
  private String oldPassword;

  /**
   * The new password to be set. Must be between 8 and 100 characters and contain only allowed
   * characters.
   */
  @Size(min = 8, max = 100, message = "Password must be at between 8 and 100 characters long")
  @Pattern(
      regexp = "^(?=.*[\\p{L}])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$",
      message = "Invalid password format"
  )
  private String newPassword;

  /**
   * Confirmation of the new password. Must match the new password and meet the same validation
   * requirements.
   */
  @Size(min = 8, max = 100, message = "Password must be at between 8 and 100 characters long")
  @Pattern(
      regexp = "^[A-Za-z0-9\\p{L}\\p{M}\\p{P}\\p{S}]+$",
      message = "Invalid password format"
  )
  private String confirmNewPassword;
}
