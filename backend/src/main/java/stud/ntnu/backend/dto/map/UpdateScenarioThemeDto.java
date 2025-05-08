package stud.ntnu.backend.dto.map;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for updating an existing scenario theme.
 * <p>
 * This DTO contains fields for updating a scenario theme's properties. The ID field
 * is required, while all other fields are optional. If a field is null, the corresponding
 * field in the existing scenario theme will not be updated.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateScenarioThemeDto {

    /**
     * The unique identifier of the scenario theme to update.
     * This field is required and cannot be null.
     */
    @NotNull(message = "ID is required")
    private Integer id;

    /**
     * The name of the scenario theme.
     */
    private String name;

    /**
     * A detailed description of the scenario theme.
     */
    private String description;

    /**
     * Description of the state or condition before the scenario.
     */
    private String before;

    /**
     * Description of the state or condition during the scenario.
     */
    private String under;

    /**
     * Description of the state or condition after the scenario.
     */
    private String after;

    /**
     * The current status of the scenario theme.
     */
    private String status;
}