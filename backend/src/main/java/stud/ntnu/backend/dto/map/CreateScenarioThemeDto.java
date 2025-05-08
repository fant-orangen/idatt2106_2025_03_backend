package stud.ntnu.backend.dto.map;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for creating a new scenario theme.
 * <p>
 * This DTO contains all necessary information to create a new scenario theme in the system,
 * including its name, description, and various state descriptions for different phases
 * of the scenario.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateScenarioThemeDto {

    /**
     * The name of the scenario theme.
     * This field is required and cannot be null.
     */
    @NotNull(message = "Name is required")
    private String name;

    /**
     * A detailed description of the scenario theme.
     * This field is optional.
     */
    private String description;

    /**
     * Description of the situation before the scenario occurs.
     * This field is optional.
     */
    private String before;

    /**
     * Description of the situation during the scenario.
     * This field is optional.
     */
    private String under;

    /**
     * Description of the situation after the scenario has occurred.
     * This field is optional.
     */
    private String after;
}