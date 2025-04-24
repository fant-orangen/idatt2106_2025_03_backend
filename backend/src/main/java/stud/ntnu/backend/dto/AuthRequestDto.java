package stud.ntnu.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for authentication requests. Contains the email and password for user
 * login.
 */
public class AuthRequestDto {

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

  // Getters and setters
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
