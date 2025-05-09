package stud.ntnu.backend.dto.household;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing a member of a household. This class encapsulates the
 * essential information about a household member, including their identification details and
 * administrative status.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdMemberDto {

  /**
   * The unique identifier of the household member.
   */
  private Integer id;

  /**
   * The email address of the household member.
   */
  private String email;

  /**
   * The first name of the household member.
   */
  private String firstName;

  /**
   * The last name of the household member.
   */
  private String lastName;

  /**
   * Flag indicating whether the member has administrative privileges in the household.
   */
  private boolean isAdmin;
}