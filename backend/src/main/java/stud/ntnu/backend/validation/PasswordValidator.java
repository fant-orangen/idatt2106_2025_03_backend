package stud.ntnu.backend.validation;

import java.util.regex.Pattern;
import lombok.NoArgsConstructor;

/**
 * Utility class for validating passwords according to security requirements.
 * 
 * <p>This validator enforces the following password rules:
 * <ul>
 *   <li>Minimum length of 8 characters</li>
 *   <li>At least one letter (any Unicode letter)</li>
 *   <li>At least one digit</li>
 *   <li>At least one special character from the set: !@#$%^&*()_+-=[]{};':"\|,.<>/?</li>
 * </ul>
 * 
 * <p>The validator also ensures that:
 * <ul>
 *   <li>The new password is not empty</li>
 *   <li>The new password differs from the old password</li>
 *   <li>The new password matches its confirmation</li>
 * </ul>
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class PasswordValidator {

    /**
     * Regular expression pattern for password validation.
     * Matches strings that contain at least one letter, one digit, one special character,
     * and are at least 8 characters long.
     */
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[\\p{L}])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$"
    );

    /**
     * Validates a new password against security requirements and the old password.
     *
     * @param oldPassword The user's current password
     * @param newPassword The proposed new password
     * @param confirmNewPassword Confirmation of the new password
     * @throws IllegalArgumentException if:
     *         <ul>
     *           <li>The new password is null or empty</li>
     *           <li>The new password matches the old password</li>
     *           <li>The new password doesn't match its confirmation</li>
     *           <li>The new password doesn't meet the security requirements</li>
     *         </ul>
     */
    public static void validate(String oldPassword, String newPassword, String confirmNewPassword) {
        if (newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("New password cannot be empty");
        }

        if (oldPassword.equals(newPassword)) {
            throw new IllegalArgumentException("New password cannot be the same as the old password");
        }
        if (!newPassword.equals(confirmNewPassword)) {
            throw new IllegalArgumentException("New password and confirmation do not match");
        }

        if (!PASSWORD_PATTERN.matcher(newPassword).matches()) {
            throw new IllegalArgumentException(
                    "Password must be at least 8 characters long and contain at least one letter, one digit, and one special character"
            );
        }
    }
}