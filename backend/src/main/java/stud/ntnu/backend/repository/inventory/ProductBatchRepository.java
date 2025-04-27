package stud.ntnu.backend.repository.inventory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
