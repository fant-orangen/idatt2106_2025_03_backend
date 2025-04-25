package stud.ntnu.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.inventory.ProductBatch;

/**
 * Repository interface for ProductBatch entity operations.
 */
@Repository
public interface ProductBatchRepository extends JpaRepository<ProductBatch, Integer> {
    // Basic CRUD operations are provided by JpaRepository
    // Custom query methods can be added as needed
}