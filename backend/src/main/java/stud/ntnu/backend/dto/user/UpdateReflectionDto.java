package stud.ntnu.backend.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for updating an existing reflection. This class contains the necessary
 * fields to update a reflection's content, sharing status, and optionally associate it with a
 * crisis event.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReflectionDto {

  /**
   * The content of the reflection. Must not be blank.
   */
  @NotBlank(message = "Content is required")
  private String content;

  /**
   * Indicates whether the reflection is shared or private. Must not be null.
   */
  @NotNull(message = "Shared status is required")
  private Boolean shared;

  /**
   * Optional reference to a crisis event ID. Can be null if the reflection is not associated with
   * any crisis event.
   */
  private Integer crisisEventId;
}
