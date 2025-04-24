package stud.ntnu.backend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object for authentication requests. Contains the email and password for user
 * login.
 */
@Setter
@Getter
public class AuthRequestDto {

  // Getters and setters
  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  private String email;

  @NotBlank(message = "Password is required")
  private String password;

  // Default constructor
  public AuthRequestDto() {
  }

  // Constructor with parameters
  public AuthRequestDto(String email, String password) {
    this.email = email;
    this.password = password;
  }

}
