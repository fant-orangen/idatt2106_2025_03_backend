package stud.ntnu.backend.dto.inventory;

import lombok.Data;

@Data
public class AddBatchToGroupRequestDto {
    private Integer batchId;
    private Integer groupId;
} 