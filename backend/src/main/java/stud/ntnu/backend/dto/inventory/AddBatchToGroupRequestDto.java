package stud.ntnu.backend.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for adding a batch to a group.
 * This class represents the request payload when adding a batch to a group in the inventory system.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddBatchToGroupRequestDto {

    /**
     * The unique identifier of the batch to be added to the group.
     */
    private Integer batchId;

    /**
     * The unique identifier of the group to which the batch will be added.
     */
    private Integer groupId;
}