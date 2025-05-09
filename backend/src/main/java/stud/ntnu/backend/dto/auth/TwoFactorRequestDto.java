package stud.ntnu.backend.dto.auth;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object for two-factor authentication requests. This class represents the data
 * structure used for handling two-factor authentication verification requests, containing the
 * user's email and the verification code.
 */
@Getter
@Setter
public class TwoFactorRequestDto {

  /**
   * The email address of the user requesting two-factor authentication.
   */
  private String email;

  /**
   * The verification code entered by the user for two-factor authentication.
   */
  private Integer code;

  /**
   * Default constructor required for Jackson deserialization.
   */
  public TwoFactorRequestDto() {
  }

  /**
   * Constructor with parameters for creating a new two-factor authentication request.
   *
   * @param email The email address of the user
   * @param code  The verification code entered by the user
   */
  public TwoFactorRequestDto(String email, Integer code) {
    this.email = email;
    this.code = code;
  }
}