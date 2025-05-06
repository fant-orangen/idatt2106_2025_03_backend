package stud.ntnu.backend.repository.inventory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.inventory.ProductType;
import stud.ntnu.backend.model.inventory.ProductBatch;

/**
 * Repository interface for ProductType entity operations.
 */
@Repository
public interface ProductTypeRepository extends JpaRepository<ProductType, Integer> {
    // Basic CRUD operations are provided by JpaRepository

    /**
     * Find all product types with pagination.
     *
     * @param pageable pagination information
     * @return a page of product types
     */
    Page<ProductType> findAll(Pageable pageable);

    /**
     * Find all product types for a specific household with pagination.
     *
     * @param householdId the ID of the household
     * @param pageable pagination information
     * @return a page of product types
     */
    Page<ProductType> findByHouseholdId(Integer householdId, Pageable pageable);

    /**
     * Find all product types for a specific household and category with pagination.
     *
     * @param householdId the ID of the household
     * @param category the category (e.g., 'water')
     * @param pageable pagination information
     * @return a page of product types
     */
    Page<ProductType> findByHouseholdIdAndCategory(Integer householdId, String category, Pageable pageable);


    @Query("SELECT DISTINCT pt FROM ProductType pt " +
           "JOIN ProductBatch pb ON pb.productType = pt " +
           "JOIN GroupInventoryContribution gic ON gic.product = pb " +
           "WHERE gic.group.id = :groupId")
    Page<ProductType> findContributedProductTypesByGroup(
        @Param("groupId") Integer groupId,
        Pageable pageable);
}
