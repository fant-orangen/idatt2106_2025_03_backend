package stud.ntnu.backend.repository.group;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.group.GroupInventoryContribution;
import stud.ntnu.backend.model.inventory.ProductBatch;

/**
 * Repository interface for GroupInventoryContribution entity operations.
 */
@Repository
public interface GroupInventoryContributionRepository extends JpaRepository<GroupInventoryContribution, Integer> {
    // Basic CRUD operations are provided by JpaRepository
    // Custom query methods can be added as needed

    @Query("SELECT gic.product FROM GroupInventoryContribution gic WHERE gic.group.id = :groupId AND gic.product.productType.id = :productTypeId")
    Page<ProductBatch> findContributedProductBatchesByGroupAndProductType(@Param("groupId") Integer groupId, @Param("productTypeId") Integer productTypeId, Pageable pageable);
}