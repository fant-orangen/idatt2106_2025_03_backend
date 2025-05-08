package stud.ntnu.backend.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for creating a new reflection entry.
 * This class encapsulates the data required to create a reflection,
 * including its content, sharing status, and optional association with a crisis event.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateReflectionDto {

    /**
     * The content of the reflection.
     * Must not be blank or empty.
     */
    @NotBlank(message = "Content is required")
    private String content;

    /**
     * Indicates whether the reflection is shared or private.
     * Must not be null.
     */
    @NotNull(message = "Shared status is required")
    private Boolean shared;

    /**
     * Optional identifier for the associated crisis event.
     * Can be null if the reflection is not linked to any specific crisis event.
     */
    private Integer crisisEventId;
}
