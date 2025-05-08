package stud.ntnu.backend.dto.quiz;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing a preview of a quiz.
 * This class contains essential information about a quiz that can be displayed in a list or preview context.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizPreviewDto {
    /** Unique identifier for the quiz */
    private Long id;
    
    /** Name/title of the quiz */
    private String name;
    
    /** Brief description of the quiz content */
    private String description;
    
    /** Current status of the quiz (e.g., DRAFT, PUBLISHED, ARCHIVED) */
    private String status;
    
    /** Total number of questions in the quiz */
    private Long questionCount;
    
    /** Timestamp when the quiz was created */
    private LocalDateTime createdAt;
}