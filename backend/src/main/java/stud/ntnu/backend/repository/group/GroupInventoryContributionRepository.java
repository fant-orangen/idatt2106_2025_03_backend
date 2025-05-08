package stud.ntnu.backend.repository.group;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.group.GroupInventoryContribution;
import stud.ntnu.backend.model.group.Group;
import stud.ntnu.backend.model.inventory.ProductBatch;
import stud.ntnu.backend.model.inventory.ProductType;
import java.util.Optional;

/**
 * Repository interface for GroupInventoryContribution entity operations.
 * Handles database operations for group inventory contributions, including finding, 
 * counting, and managing product batches and types contributed to groups.
 */
@Repository
public interface GroupInventoryContributionRepository extends JpaRepository<GroupInventoryContribution, Integer> {
    // Basic CRUD operations are provided by JpaRepository
    // Custom query methods can be added as needed

    /**
     * Finds all product batches of a specific type that have been contributed to a group.
     *
     * @param groupId The ID of the group to search in
     * @param productTypeId The ID of the product type to filter by
     * @param pageable Pagination information
     * @return A page of ProductBatch entities that match the criteria
     */
    @Query("SELECT DISTINCT pb FROM ProductBatch pb " +
           "JOIN GroupInventoryContribution gic ON gic.product = pb " +
           "WHERE gic.group.id = :groupId " +
           "AND pb.productType.id = :productTypeId " +
           "AND gic.product IS NOT NULL")
    Page<ProductBatch> findContributedProductBatchesByGroupAndProductType(
        @Param("groupId") Integer groupId,
        @Param("productTypeId") Integer productTypeId,
        Pageable pageable);

    /**
     * Searches for product types contributed to a group by name.
     *
     * @param groupId The ID of the group to search in
     * @param householdId The ID of the household to filter by
     * @param searchTerm The search term to match against product names
     * @param pageable Pagination information
     * @return A page of ProductType entities that match the search criteria
     */
    @Query("SELECT DISTINCT pt FROM ProductType pt " +
           "JOIN ProductBatch pb ON pb.productType = pt " +
           "JOIN GroupInventoryContribution gic ON gic.product = pb " +
           "WHERE gic.group.id = :groupId " +
           "AND gic.product IS NOT NULL " +
           "AND LOWER(pt.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<ProductType> findContributedProductTypesByGroupAndNameContaining(
        @Param("groupId") Integer groupId,
        @Param("householdId") Integer householdId,
        @Param("searchTerm") String searchTerm,
        Pageable pageable);

    /**
     * Checks if a product batch has been contributed to any group.
     *
     * @param productBatchId The ID of the product batch to check
     * @return true if the batch has been contributed, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(gic) > 0 THEN true ELSE false END FROM GroupInventoryContribution gic " +
           "WHERE gic.product.id = :productBatchId")
    boolean existsByProductBatchId(@Param("productBatchId") Integer productBatchId);

    /**
     * Deletes all contributions from a specific household to a specific group.
     *
     * @param groupId The ID of the group
     * @param householdId The ID of the household
     */
    @Modifying
    @Query("DELETE FROM GroupInventoryContribution gic " +
           "WHERE gic.group.id = :groupId AND gic.household.id = :householdId")
    void deleteByGroupIdAndHouseholdId(@Param("groupId") Integer groupId, @Param("householdId") Integer householdId);

    /**
     * Calculates the total number of units for a specific product type in a group.
     *
     * @param productTypeId The ID of the product type
     * @param groupId The ID of the group
     * @return The total number of units, or 0 if none found
     */
    @Query("SELECT COALESCE(SUM(pb.number), 0) FROM GroupInventoryContribution gic " +
           "JOIN gic.product pb " +
           "WHERE gic.group.id = :groupId " +
           "AND pb.productType.id = :productTypeId")
    Integer sumTotalUnitsForProductTypeAndGroup(
        @Param("productTypeId") Integer productTypeId,
        @Param("groupId") Integer groupId);

    /**
     * Finds a group inventory contribution by product batch ID.
     *
     * @param productBatchId The ID of the product batch
     * @return An Optional containing the GroupInventoryContribution if found
     */
    @Query("SELECT gic FROM GroupInventoryContribution gic " +
           "WHERE gic.product.id = :productBatchId")
    Optional<GroupInventoryContribution> findByProductBatchId(@Param("productBatchId") Integer productBatchId);
}