package stud.ntnu.backend.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddBatchToGroupRequestDto {

  private Integer batchId;
  private Integer groupId;
} 