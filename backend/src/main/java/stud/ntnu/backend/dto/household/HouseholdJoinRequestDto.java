package stud.ntnu.backend.dto.household;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) representing a request to join a household. This DTO contains the
 * invitation token required to join a specific household. The token is used to validate and process
 * the join request.
 */
@Setter
@Getter
public class HouseholdJoinRequestDto {

  /**
   * The invitation token required to join a household. This token is generated when creating a
   * household invitation.
   */
  private String token;

  /**
   * Default constructor for creating an empty join request.
   */
  public HouseholdJoinRequestDto() {
  }

  /**
   * Constructor for creating a join request with a specific token.
   *
   * @param token The invitation token required to join the household
   */
  public HouseholdJoinRequestDto(String token) {
    this.token = token;
  }
}