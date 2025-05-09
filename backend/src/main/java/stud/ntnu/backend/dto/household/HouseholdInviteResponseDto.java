package stud.ntnu.backend.dto.household;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for household invitation responses. This class encapsulates the data
 * returned when a household invitation is created, including the invitation token, invitee details,
 * and household information.
 */
@Getter
@Setter
public class HouseholdInviteResponseDto {

  /**
   * The unique token generated for this invitation. This token is used to validate and process the
   * invitation.
   */
  private String token;

  /**
   * The email address of the user being invited to join the household.
   */
  private String invitedEmail;

  /**
   * The unique identifier of the household the user is being invited to.
   */
  private Integer householdId;

  /**
   * The name of the household the user is being invited to.
   */
  private String householdName;

  /**
   * The expiration date and time of the invitation in ISO-8601 format.
   */
  private String expiresAt;

  /**
   * Default constructor required for JSON deserialization.
   */
  public HouseholdInviteResponseDto() {
  }

  /**
   * Constructs a new HouseholdInviteResponseDto with all fields.
   *
   * @param token         the unique invitation token
   * @param invitedEmail  the email address of the invitee
   * @param householdId   the ID of the household
   * @param householdName the name of the household
   * @param expiresAt     the expiration date and time of the invitation
   */
  public HouseholdInviteResponseDto(String token, String invitedEmail, Integer householdId,
      String householdName, String expiresAt) {
    this.token = token;
    this.invitedEmail = invitedEmail;
    this.householdId = householdId;
    this.householdName = householdName;
    this.expiresAt = expiresAt;
  }
}