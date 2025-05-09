package stud.ntnu.backend.repository.group;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import stud.ntnu.backend.model.group.GroupInventoryContribution;
import stud.ntnu.backend.model.inventory.ProductBatch;
import stud.ntnu.backend.model.inventory.ProductType;

/**
 * Repository interface for managing GroupInventoryContribution entities. Provides methods for
 * querying and managing group inventory contributions, including operations for product batches,
 * types, and household contributions.
 */
@Repository
public interface GroupInventoryContributionRepository extends
    JpaRepository<GroupInventoryContribution, Integer> {

  /**
   * Retrieves all product batches of a specific type that have been contributed to a group. Results
   * are paginated and filtered by both group and product type.
   *
   * @param groupId       The ID of the group to search in
   * @param productTypeId The ID of the product type to filter by
   * @param pageable      Pagination and sorting parameters
   * @return A page of ProductBatch entities matching the criteria
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
   * Verifies if a specific product batch has been contributed to any group.
   *
   * @param productBatchId The ID of the product batch to check
   * @return true if the batch has been contributed to any group, false otherwise
   */
  @Query(
      "SELECT CASE WHEN COUNT(gic) > 0 THEN true ELSE false END FROM GroupInventoryContribution gic "
          +
          "WHERE gic.product.id = :productBatchId")
  boolean existsByProductBatchId(@Param("productBatchId") Integer productBatchId);

  /**
   * Removes all inventory contributions from a specific household to a specific group.
   *
   * @param groupId     The ID of the target group
   * @param householdId The ID of the household whose contributions should be removed
   */
  @Modifying
  @Query("DELETE FROM GroupInventoryContribution gic " +
      "WHERE gic.group.id = :groupId AND gic.household.id = :householdId")
  void deleteByGroupIdAndHouseholdId(@Param("groupId") Integer groupId,
      @Param("householdId") Integer householdId);

  /**
   * Calculates the total number of units for a specific product type within a group. Returns 0 if
   * no contributions exist for the specified product type.
   *
   * @param productTypeId The ID of the product type to sum
   * @param groupId       The ID of the group to search in
   * @return The total number of units for the specified product type in the group
   */
  @Query("SELECT COALESCE(SUM(pb.number), 0) FROM GroupInventoryContribution gic " +
      "JOIN gic.product pb " +
      "WHERE gic.group.id = :groupId " +
      "AND pb.productType.id = :productTypeId")
  Integer sumTotalUnitsForProductTypeAndGroup(
      @Param("productTypeId") Integer productTypeId,
      @Param("groupId") Integer groupId);

  /**
   * Locates a specific group inventory contribution by its associated product batch ID.
   *
   * @param productBatchId The ID of the product batch to search for
   * @return An Optional containing the GroupInventoryContribution if found, empty otherwise
   */
  @Query("SELECT gic FROM GroupInventoryContribution gic " +
      "WHERE gic.product.id = :productBatchId")
  Optional<GroupInventoryContribution> findByProductBatchId(
      @Param("productBatchId") Integer productBatchId);

  /**
   * Retrieves all unique product type IDs that have been contributed to a specific group.
   *
   * @param groupId The ID of the group to search in
   * @return A list of product type IDs that have been contributed to the group
   */
  @Query("SELECT DISTINCT pt.id FROM ProductType pt " +
      "JOIN ProductBatch pb ON pb.productType = pt " +
      "JOIN GroupInventoryContribution gic ON gic.product = pb " +
      "WHERE gic.group.id = :groupId " +
      "AND gic.product IS NOT NULL")
  List<Integer> findProductTypeIdsContributedToGroup(@Param("groupId") Integer groupId);
}
