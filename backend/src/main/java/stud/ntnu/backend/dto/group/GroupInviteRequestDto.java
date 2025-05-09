package stud.ntnu.backend.dto.group;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for group invitation requests. Contains the necessary information to
 * invite a household to a group.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupInviteRequestDto {

  /**
   * The name of the household to invite. This field is required and cannot be blank.
   */
  @NotBlank(message = "Household name is required")
  private String householdName;

  /**
   * The ID of the group to invite the household to. This field is required.
   */
  @NotNull(message = "Group ID is required")
  private Integer groupId;
} 