package stud.ntnu.backend.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for updating a user's profile information.
 */
@Setter
@Getter
public class UserUpdateDto {

  // Getters and setters
  private String firstName;
  private String lastName;
  private String homeAddress;
  private BigDecimal homeLatitude;
  private BigDecimal homeLongitude;

  // Default constructor
  public UserUpdateDto() {
  }

  // Constructor with all fields
  public UserUpdateDto(String firstName, String lastName, String homeAddress,
      BigDecimal homeLatitude, BigDecimal homeLongitude) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.homeAddress = homeAddress;
    this.homeLatitude = homeLatitude;
    this.homeLongitude = homeLongitude;
  }

}