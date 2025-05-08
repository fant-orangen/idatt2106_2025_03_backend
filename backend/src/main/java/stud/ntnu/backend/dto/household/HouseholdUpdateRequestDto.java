package stud.ntnu.backend.dto.household;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) representing the request payload for updating a household.
 * This class contains the fields that can be modified when updating an existing household.
 * All fields are required and must not be blank.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdUpdateRequestDto {

    /**
     * The name of the household.
     * Must not be blank.
     */
    @NotBlank(message = "Name is required")
    private String name;

    /**
     * The physical address of the household.
     * Must not be blank.
     */
    @NotBlank(message = "Address is required")
    private String address;
}
