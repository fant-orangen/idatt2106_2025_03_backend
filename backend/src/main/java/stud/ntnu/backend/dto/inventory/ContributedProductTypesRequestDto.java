package stud.ntnu.backend.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContributedProductTypesRequestDto {

  private Integer groupId;
  private String category;
} 