package stud.ntnu.backend.repository.group;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.group.GroupInventoryContribution;
import stud.ntnu.backend.model.inventory.ProductBatch;
import stud.ntnu.backend.model.inventory.ProductType;

/**
 * Repository interface for GroupInventoryContribution entity operations.
 */
@Repository
public interface GroupInventoryContributionRepository extends JpaRepository<GroupInventoryContribution, Integer> {
    // Basic CRUD operations are provided by JpaRepository
    // Custom query methods can be added as needed

    @Query("SELECT DISTINCT pb FROM ProductBatch pb " +
           "JOIN GroupInventoryContribution gic ON gic.product = pb " +
           "WHERE gic.group.id = :groupId " +
           "AND pb.productType.id = :productTypeId " +
           "AND gic.product IS NOT NULL")
    Page<ProductBatch> findContributedProductBatchesByGroupAndProductType(
        @Param("groupId") Integer groupId,
        @Param("productTypeId") Integer productTypeId,
        Pageable pageable);

    @Query("SELECT DISTINCT pt FROM ProductType pt " +
           "JOIN ProductBatch pb ON pb.productType = pt " +
           "JOIN GroupInventoryContribution gic ON gic.product = pb " +
           "WHERE gic.group.id = :groupId " +
           "AND gic.household.id = :householdId " +
           "AND gic.product IS NOT NULL " +
           "AND LOWER(pt.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<ProductType> findContributedProductTypesByGroupAndHouseholdAndNameContaining(
        @Param("groupId") Integer groupId,
        @Param("householdId") Integer householdId,
        @Param("searchTerm") String searchTerm,
        Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(gic) > 0 THEN true ELSE false END FROM GroupInventoryContribution gic " +
           "WHERE gic.product.id = :productBatchId")
    boolean existsByProductBatchId(@Param("productBatchId") Integer productBatchId);
}