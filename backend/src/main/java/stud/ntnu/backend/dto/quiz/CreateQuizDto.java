package stud.ntnu.backend.dto.quiz;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for creating a new quiz.
 * <p>
 * This DTO contains all the necessary information required to create a new quiz,
 * including its name, description, and status.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateQuizDto {

    /**
     * The name of the quiz.
     * This field is required.
     */
    private String name;

    /**
     * A detailed description of the quiz.
     * This field is optional.
     */
    private String description;

    /**
     * The current status of the quiz.
     * This field is optional and defaults to 'active' if not provided.
     */
    private String status;
}