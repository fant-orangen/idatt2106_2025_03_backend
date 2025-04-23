package stud.ntnu.backend.dto;

/**
 * Data Transfer Object for authentication requests. Contains the email and password for user
 * login.
 */
public class AuthRequestDto {

  private String email;
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