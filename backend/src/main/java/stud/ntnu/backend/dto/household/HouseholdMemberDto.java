package stud.ntnu.backend.dto.household;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdMemberDto {

  private Integer id;
  private String email;
  private String firstName;
  private String lastName;
  private boolean isAdmin;
} 