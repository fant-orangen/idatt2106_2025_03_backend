package stud.ntnu.backend.dto.map;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for creating a new meeting place. This class represents the data
 * structure required to create a meeting place, including its name, geographical coordinates, and
 * address.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateMeetingPlaceDto {

  /**
   * The name of the meeting place. This field is required and cannot be blank.
   */
  @NotBlank(message = "Name is required")
  private String name;

  /**
   * The latitude coordinate of the meeting place. Represents the north-south position on Earth.
   */
  private BigDecimal latitude;

  /**
   * The longitude coordinate of the meeting place. Represents the east-west position on Earth.
   */
  private BigDecimal longitude;

  /**
   * The physical address of the meeting place.
   */
  private String address;
}