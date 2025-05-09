package stud.ntnu.backend.dto.user;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) representing a user's preferences. This class is used to transfer user
 * preference data between layers of the application. It includes settings such as location sharing
 * preferences and can be extended to include additional user preference settings in the future.
 */
@Setter
@Getter
public class UserPreferencesDto {

  /**
   * Indicates whether the user has enabled location sharing. When true, the user's location can be
   * shared with other users. When false, location sharing is disabled.
   */
  private Boolean locationSharingEnabled;

  /**
   * Default constructor for UserPreferencesDto. Creates a new instance with default values.
   */
  public UserPreferencesDto() {
  }

  /**
   * Constructor for UserPreferencesDto with all fields.
   *
   * @param locationSharingEnabled The user's location sharing preference
   */
  public UserPreferencesDto(Boolean locationSharingEnabled) {
    this.locationSharingEnabled = locationSharingEnabled;
  }
}