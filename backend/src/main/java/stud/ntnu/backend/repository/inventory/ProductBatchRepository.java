package stud.ntnu.backend.repository.inventory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.inventory.ProductBatch;

/**
 * Repository interface for ProductBatch entity operations.
 */
@Repository
public interface ProductBatchRepository extends JpaRepository<ProductBatch, Integer> {
    // Basic CRUD operations are provided by JpaRepository

    /**
     * Find all product batches for a given product type.
     *
     * @param productTypeId the ID of the product type
     * @param pageable pagination information
     * @return a page of product batches
     */
    Page<ProductBatch> findByProductTypeId(Integer productTypeId, Pageable pageable);

    /**
     * Sum the total number of units for a given product type.
     *
     * @param productTypeId the ID of the product type
     * @return the total number of units
     */
    @Query("SELECT COALESCE(SUM(pb.number), 0) FROM ProductBatch pb WHERE pb.productType.id = :productTypeId")
    Integer sumNumberByProductTypeId(@Param("productTypeId") Integer productTypeId);

    /**
     * Sum the total number of litres for all product batches where the product type category is 'water' and the unit is 'l'.
     *
     * @return the total number of litres
     */
    @Query("SELECT COALESCE(SUM(pb.number), 0) FROM ProductBatch pb WHERE pb.productType.category = 'water' AND pb.productType.unit = 'l'")
    Integer sumTotalLitresOfWater();

    /**
     * Sum the total number of litres for all product batches where the product type category is 'water', unit is 'l', and household matches.
     *
     * @param householdId the ID of the household
     * @return the total number of litres
     */
    @Query("SELECT COALESCE(SUM(pb.number), 0) FROM ProductBatch pb WHERE pb.productType.category = 'water' AND pb.productType.unit = 'l' AND pb.productType.household.id = :householdId")
    Integer sumTotalLitresOfWaterByHousehold(@Param("householdId") Integer householdId);
}
