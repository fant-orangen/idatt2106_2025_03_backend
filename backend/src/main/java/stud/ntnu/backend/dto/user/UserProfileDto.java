package stud.ntnu.backend.dto.user;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for returning a user's profile information.
 */
@Setter
@Getter
public class UserProfileDto {

  // Getters and setters
  private Integer id;
  private String email;
  private String firstName;
  private String lastName;
  private String homeAddress;
  private BigDecimal homeLatitude;
  private BigDecimal homeLongitude;
  private Boolean locationSharingEnabled;
  private Boolean notificationsEnabled;
  private Boolean twoFactorAuthenticationEnabled;
  private Boolean emailVerified;
  private Integer householdId;
  private String householdName;

  // Default constructor
  public UserProfileDto() {
  }

  // Constructor with all fields
  public UserProfileDto(Integer id, String email, String firstName, String lastName,
      String homeAddress, BigDecimal homeLatitude, BigDecimal homeLongitude,
      Boolean locationSharingEnabled, Boolean notificationsEnabled, Boolean twoFactorAuthenticationEnabled, Boolean emailVerified,
      Integer householdId, String householdName) {
    this.id = id;
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
    this.homeAddress = homeAddress;
    this.homeLatitude = homeLatitude;
    this.homeLongitude = homeLongitude;
    this.locationSharingEnabled = locationSharingEnabled;
    this.notificationsEnabled = notificationsEnabled;
    this.twoFactorAuthenticationEnabled = twoFactorAuthenticationEnabled;
    this.emailVerified = emailVerified;
    this.householdId = householdId;
    this.householdName = householdName;
  }

}