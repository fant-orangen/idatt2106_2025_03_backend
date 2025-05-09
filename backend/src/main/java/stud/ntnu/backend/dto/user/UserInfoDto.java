package stud.ntnu.backend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) representing basic user identification information. This class
 * encapsulates essential user identifiers including email and user ID.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDto {

  /**
   * The user's email address.
   */
  private String email;

  /**
   * The unique identifier for the user.
   */
  private Integer userId;
}
