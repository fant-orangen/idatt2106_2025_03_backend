package stud.ntnu.backend.dto.map;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for creating a new scenario theme.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateScenarioThemeDto {

  @NotNull(message = "Name is required")
  private String name;
  private String description;
  private String instructions;
} 