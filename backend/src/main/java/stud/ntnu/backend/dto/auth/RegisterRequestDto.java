package stud.ntnu.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import lombok.Getter;
import stud.ntnu.backend.validation.CoordinatesPair;

/**
 * Data Transfer Object for user registration requests. Extends AuthRequestDto to include additional
 * user information required for registration.
 */
@Getter
@CoordinatesPair(latitudeField = "homeLatitude", longitudeField = "homeLongitude",
    message = "Both latitude and longitude must be provided together")
public class RegisterRequestDto extends AuthRequestDto {

  @NotBlank(message = "First name is required")
  private String firstName;

  @NotBlank(message = "Last name is required")
  private String lastName;

  // Optional field, no validation needed
  private String homeAddress;

  // Both latitude and longitude must be present together or both absent
  private BigDecimal homeLatitude;
  private BigDecimal homeLongitude;

  // Default constructor
  public RegisterRequestDto() {
    super();
  }

  // Constructor with all fields
  public RegisterRequestDto(String email, String password, String firstName, String lastName, String homeAddress,
      BigDecimal homeLatitude, BigDecimal homeLongitude) {
    super(email, password);
    this.firstName = firstName;
    this.lastName = lastName;
    this.homeAddress = homeAddress;
    this.homeLatitude = homeLatitude;
    this.homeLongitude = homeLongitude;
  }

  // Constructor with combined name for backward compatibility
  public RegisterRequestDto(String email, String password, String name, String homeAddress,
      BigDecimal homeLatitude, BigDecimal homeLongitude) {
    super(email, password);
    if (name != null) {
      String[] parts = name.split(" ", 2);
      this.firstName = parts[0];
      this.lastName = parts.length > 1 ? parts[1] : "";
    }
    this.homeAddress = homeAddress;
    this.homeLatitude = homeLatitude;
    this.homeLongitude = homeLongitude;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  // For backward compatibility
  public void setName(String name) {
    if (name != null) {
      String[] parts = name.split(" ", 2);
      this.firstName = parts[0];
      this.lastName = parts.length > 1 ? parts[1] : "";
    }
  }

  // For backward compatibility
  public String getName() {
    if (firstName != null && lastName != null) {
      return firstName + " " + lastName;
    } else if (firstName != null) {
      return firstName;
    } else if (lastName != null) {
      return lastName;
    }
    return null;
  }

  public void setHomeAddress(String homeAddress) {
    this.homeAddress = homeAddress;
  }

  public void setHomeLatitude(BigDecimal homeLatitude) {
    this.homeLatitude = homeLatitude;
  }

  public void setHomeLongitude(BigDecimal homeLongitude) {
    this.homeLongitude = homeLongitude;
  }
}
