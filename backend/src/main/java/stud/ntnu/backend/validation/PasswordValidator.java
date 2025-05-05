package stud.ntnu.backend.validation;


import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

/**
 * Utility class for validating passwords. The password must be at least 8 characters long and
 * contain at least one letter, one digit, and one special character.
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class PasswordValidator {

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[\\p{L}])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$"
    );

    /**
     * Validates the new password against the old password and checks if it meets the required
     * criteria.
     *
     * @param oldPassword          The old password.
     * @param newPassword          The new password to be validated.
     * @param confirmNewPassword   Confirmation of the new password.
     * @throws IllegalArgumentException if the new password is invalid or does not meet the criteria.
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