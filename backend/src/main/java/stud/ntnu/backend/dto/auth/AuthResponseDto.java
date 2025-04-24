package stud.ntnu.backend.dto.auth;

/**
 * Data Transfer Object for authentication responses. Contains the JWT token and user information
 * returned after successful authentication.
 */
public class AuthResponseDto {

  private String token;
  private Integer userId;
  private String email;
  private String role;
  private Integer householdId;

  // Default constructor
  public AuthResponseDto() {
  }

  // Constructor with token only
  public AuthResponseDto(String token) {
    this.token = token;
  }

  // Constructor with all fields
  public AuthResponseDto(String token, Integer userId, String email, String role,
      Integer householdId) {
    this.token = token;
    this.userId = userId;
    this.email = email;
    this.role = role;
    this.householdId = householdId;
  }

  // Getters and setters
  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public Integer getHouseholdId() {
    return householdId;
  }

  public void setHouseholdId(Integer householdId) {
    this.householdId = householdId;
  }
}