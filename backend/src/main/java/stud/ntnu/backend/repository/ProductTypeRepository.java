package stud.ntnu.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.inventory.ProductType;

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
}
