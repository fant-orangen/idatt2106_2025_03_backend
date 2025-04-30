package stud.ntnu.backend.dto.user;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for updating a user's preferences.
 */
@Setter
@Getter
public class UserPreferencesDto {

  private Boolean locationSharingEnabled;
  private Boolean notificationsEnabled;
  private Boolean twoFactorAuthenticationEnabled;

  // Add more preferences as needed, such as notification settings

  // Default constructor
  public UserPreferencesDto() {
  }

  // Constructor with all fields
  public UserPreferencesDto(Boolean locationSharingEnabled, Boolean notificationsEnabled,
      Boolean twoFactorAuthenticationEnabled) {
    this.locationSharingEnabled = locationSharingEnabled;
    this.notificationsEnabled = notificationsEnabled;
    this.twoFactorAuthenticationEnabled = twoFactorAuthenticationEnabled;
  }

}