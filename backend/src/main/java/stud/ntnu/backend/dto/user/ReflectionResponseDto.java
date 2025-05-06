package stud.ntnu.backend.dto.user;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stud.ntnu.backend.model.user.Reflection;
import stud.ntnu.backend.model.map.CrisisEvent;

/**
 * DTO for returning reflection data.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReflectionResponseDto {

  private Integer id;
  private Integer userId;
  private String userFirstName;
  private String userLastName;
  private String content;
  private Boolean shared;
  private Boolean deleted;
  private LocalDateTime createdAt;

  // Crisis event information
  private Integer crisisEventId;
  private String crisisEventName;

  /**
   * Converts a Reflection entity to a ReflectionResponseDto.
   *
   * @param reflection the reflection entity
   * @return the reflection response DTO
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
