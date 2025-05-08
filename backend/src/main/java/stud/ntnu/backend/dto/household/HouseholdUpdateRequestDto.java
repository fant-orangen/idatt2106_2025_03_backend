package stud.ntnu.backend.dto.household;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for updating a household.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdUpdateRequestDto {

  @NotBlank(message = "Name is required")
  private String name;

  @NotBlank(message = "Address is required")
  private String address;
}
