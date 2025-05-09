package stud.ntnu.backend.dto.user;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import stud.ntnu.backend.model.map.CrisisEvent;
import stud.ntnu.backend.model.user.Reflection;

/**
 * Data Transfer Object (DTO) representing a reflection response. This class encapsulates reflection
 * data for transfer between layers of the application, including user information, reflection
 * content, and associated crisis event details.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReflectionResponseDto {

  /**
   * Unique identifier for the reflection.
   */
  private Integer id;

  /**
   * Identifier of the user who created this reflection.
   */
  private Integer userId;

  /**
   * First name of the user who created this reflection.
   */
  private String userFirstName;

  /**
   * Last name of the user who created this reflection.
   */
  private String userLastName;

  /**
   * The content/body of the reflection.
   */
  private String content;

  /**
   * Indicates whether the reflection is shared or private.
   */
  private Boolean shared;

  /**
   * Indicates whether the reflection has been marked as deleted.
   */
  private Boolean deleted;

  /**
   * Timestamp when the reflection was created.
   */
  private LocalDateTime createdAt;

  /**
   * Identifier of the associated crisis event, if any.
   */
  private Integer crisisEventId;

  /**
   * Name of the associated crisis event, if any.
   */
  private String crisisEventName;

  /**
   * Converts a Reflection entity to a ReflectionResponseDto. This method handles the conversion of
   * entity-specific data types to their DTO representations, including the extraction of user
   * information and associated crisis event details.
   *
   * @param reflection the reflection entity to convert
   * @return a new ReflectionResponseDto instance containing the converted data
   */
  public static ReflectionResponseDto fromEntity(Reflection reflection) {
    CrisisEvent crisisEvent = reflection.getCrisisEvent();

    return new ReflectionResponseDto(
        reflection.getId(),
        reflection.getUser().getId(),
        reflection.getUser().getFirstName(),
        reflection.getUser().getLastName(),
        reflection.getContent(),
        reflection.getShared(),
        reflection.getDeleted(),
        reflection.getCreatedAt(),
        crisisEvent != null ? crisisEvent.getId() : null,
        crisisEvent != null ? crisisEvent.getName() : null
    );
  }
}
