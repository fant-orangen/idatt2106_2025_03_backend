package stud.ntnu.backend.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for requesting contributed product types. This class represents the
 * request payload when retrieving product types that have been contributed to a particular group,
 * optionally filtered by category.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContributedProductTypesRequestDto {

  /**
   * The unique identifier of the group to which the product types have been contributed.
   */
  private Integer groupId;

  /**
   * The category to filter product types by. If null, all product types will be returned.
   */
  private String category;
}