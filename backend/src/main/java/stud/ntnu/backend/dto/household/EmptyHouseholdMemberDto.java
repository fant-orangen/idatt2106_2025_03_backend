package stud.ntnu.backend.dto.household;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing an empty household member. This class is used to transfer
 * household member data without associated user information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmptyHouseholdMemberDto {

  /**
   * The unique identifier of the household member.
   */
  private Integer id;

  /**
   * The name of the household member.
   */
  private String name;

  /**
   * The type of household member (e.g., "adult", "child").
   */
  private String type;

  /**
   * A description of the household member.
   */
  private String description;

  /**
   * The daily caloric requirement for the household member in kilocalories.
   */
  private Integer kcal_requirement;
}