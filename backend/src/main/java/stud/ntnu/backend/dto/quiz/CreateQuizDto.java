package stud.ntnu.backend.dto.quiz;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateQuizDto {

  private String name;
  private String description;
  private String status; // Optional, defaults to 'active' if not provided
} 