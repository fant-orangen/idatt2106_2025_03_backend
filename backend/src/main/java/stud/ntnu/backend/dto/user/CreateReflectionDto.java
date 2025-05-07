package stud.ntnu.backend.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for creating a new reflection.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateReflectionDto {

  @NotBlank(message = "Content is required")
  private String content;

  @NotNull(message = "Shared status is required")
  private Boolean shared;

  // Optional crisis event ID
  private Integer crisisEventId;
}
