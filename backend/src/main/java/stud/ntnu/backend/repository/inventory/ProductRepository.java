package stud.ntnu.backend.repository.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.inventory.Product;

/**
 * Repository interface for Product entity operations.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
  // Basic CRUD operations are provided by JpaRepository
  // Custom query methods can be added as needed
}