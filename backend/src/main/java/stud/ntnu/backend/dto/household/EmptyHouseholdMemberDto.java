package stud.ntnu.backend.dto.household;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmptyHouseholdMemberDto {

  private Integer id;
  private String firstName;
  private String lastName;
  private String type;
  private String description;
} 