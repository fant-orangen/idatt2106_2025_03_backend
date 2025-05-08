package stud.ntnu.backend.dto.household;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmptyHouseholdMemberDto {

  private Integer id;
  private String name;
  private String type;
  private String description;
  private Integer kcal_requirement;
} 