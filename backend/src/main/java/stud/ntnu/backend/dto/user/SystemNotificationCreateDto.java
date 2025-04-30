package stud.ntnu.backend.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for creating a system notification.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SystemNotificationCreateDto {

  @NotBlank(message = "Description is required")
  private String description;
}
