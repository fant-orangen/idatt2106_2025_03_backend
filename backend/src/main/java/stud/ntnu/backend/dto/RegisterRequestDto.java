package stud.ntnu.backend.dto;

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

  @NotBlank(message = "Name is required")
  private String name;

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
  public RegisterRequestDto(String email, String password, String name, String homeAddress,
      BigDecimal homeLatitude, BigDecimal homeLongitude) {
    super(email, password);
    this.name = name;
    this.homeAddress = homeAddress;
    this.homeLatitude = homeLatitude;
    this.homeLongitude = homeLongitude;
  }

  public void setName(String name) {
    this.name = name;
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
