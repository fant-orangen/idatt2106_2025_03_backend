package stud.ntnu.backend.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for updating a user's preferences.
 */
@Setter
@Getter
public class UserPreferencesDto {

  // Getters and setters
  private Boolean locationSharingEnabled;

  // Add more preferences as needed, such as notification settings

  // Default constructor
  public UserPreferencesDto() {
  }

  // Constructor with all fields
  public UserPreferencesDto(Boolean locationSharingEnabled) {
    this.locationSharingEnabled = locationSharingEnabled;
  }

}