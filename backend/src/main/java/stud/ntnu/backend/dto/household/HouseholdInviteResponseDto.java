package stud.ntnu.backend.dto.household;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for returning a household invitation token.
 */
@Setter
@Getter
public class HouseholdInviteResponseDto {

  // Getters and setters
  private String token;
  private String invitedEmail;
  private Integer householdId;
  private String householdName;
  private String expiresAt;

  // Default constructor
  public HouseholdInviteResponseDto() {
  }

  // Constructor with all fields
  public HouseholdInviteResponseDto(String token, String invitedEmail, Integer householdId,
      String householdName, String expiresAt) {
    this.token = token;
    this.invitedEmail = invitedEmail;
    this.householdId = householdId;
    this.householdName = householdName;
    this.expiresAt = expiresAt;
  }

}