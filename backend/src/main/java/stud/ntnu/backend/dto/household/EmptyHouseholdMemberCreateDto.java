package stud.ntnu.backend.dto.household;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating an empty household member.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmptyHouseholdMemberCreateDto {

  @NotBlank(message = "Name is required")
  private String name;

  @NotBlank(message = "Type is required")
  private String type;

  private String description;
  
  private Integer kcalRequirement = 2000; // Default value matches schema.sql
}
