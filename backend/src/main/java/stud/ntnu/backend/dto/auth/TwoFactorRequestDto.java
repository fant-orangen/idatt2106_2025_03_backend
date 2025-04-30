package stud.ntnu.backend.dto.auth;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object for two-factor authentication requests.
 */
@Getter
@Setter
public class TwoFactorRequestDto {

    private String email;
    private Integer code;

    // Default constructor (required for Jackson deserialization)
    public TwoFactorRequestDto() {
    }

    // Constructor with parameters (optional, for convenience)
    public TwoFactorRequestDto(String email, Integer code) {
        this.email = email;
        this.code = code;
    }
}