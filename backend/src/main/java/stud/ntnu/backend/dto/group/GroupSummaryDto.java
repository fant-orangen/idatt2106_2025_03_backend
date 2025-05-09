package stud.ntnu.backend.dto.group;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * Data Transfer Object (DTO) for representing a summary of a group. This class provides a
 * simplified view of a group containing only essential information. It is typically used when a
 * full group representation is not required.
 */
@Data
public class GroupSummaryDto {

  /**
   * The unique identifier of the group.
   */
  private Integer id;

  /**
   * The name of the group.
   */
  private String name;

  /**
   * The date and time when the group was created.
   */
  private LocalDateTime createdAt;
}