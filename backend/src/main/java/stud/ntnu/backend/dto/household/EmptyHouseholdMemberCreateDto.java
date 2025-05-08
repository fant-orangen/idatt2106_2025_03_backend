package stud.ntnu.backend.dto.household;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for creating an empty household member.
 * This class represents the data structure used when creating a new household member
 * without any associated user account. It contains basic information about the member
 * such as their name, type, description, and daily caloric requirements.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmptyHouseholdMemberCreateDto {

    /**
     * The name of the household member.
     * This field is required and cannot be blank.
     */
    @NotBlank(message = "Name is required")
    private String name;

    /**
     * The type or category of the household member.
     * This field is required and cannot be blank.
     */
    @NotBlank(message = "Type is required")
    private String type;

    /**
     * Optional description of the household member.
     */
    private String description;
    
    /**
     * The daily caloric requirement for the household member in kilocalories.
     * Default value is set to 2000 kcal to match the database schema.
     */
    private Integer kcalRequirement = 2000;
}
