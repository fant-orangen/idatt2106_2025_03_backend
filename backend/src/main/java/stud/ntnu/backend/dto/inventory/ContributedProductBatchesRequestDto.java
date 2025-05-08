package stud.ntnu.backend.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for requesting contributed product batches.
 * This class represents the request payload when retrieving batches of a specific product type
 * that have been contributed to a particular group.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContributedProductBatchesRequestDto {

    /**
     * The unique identifier of the group to which the product batches have been contributed.
     */
    private Integer groupId;

    /**
     * The unique identifier of the product type for which to retrieve contributed batches.
     */
    private Integer productTypeId;
}