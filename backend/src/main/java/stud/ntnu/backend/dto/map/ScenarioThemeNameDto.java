package stud.ntnu.backend.dto.map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) representing the basic information of a scenario theme.
 * <p>
 * This DTO contains only the essential identifying information of a scenario theme,
 * specifically its unique identifier and name. It is typically used in situations
 * where only basic scenario theme information is required, such as in dropdown menus
 * or simple listings.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioThemeNameDto {

    /**
     * The unique identifier of the scenario theme.
     */
    private Integer id;

    /**
     * The name of the scenario theme.
     */
    private String name;
}