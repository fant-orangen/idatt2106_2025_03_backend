package stud.ntnu.backend.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for creating a system notification. This class encapsulates the data
 * required to create a new system-wide notification, including its description and validation
 * rules.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SystemNotificationCreateDto {

  /**
   * The detailed description of the system notification. This field is required and cannot be blank
   * or empty.
   */
  @NotBlank(message = "Description is required")
  private String description;
}
