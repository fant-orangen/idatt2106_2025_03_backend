package stud.ntnu.backend.dto.map;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for updating a scenario theme.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateScenarioThemeDto {
    @NotNull(message = "ID is required")
    private Integer id;
    private String name;
    private String description;
    private String instructions;
    private String status;
} 