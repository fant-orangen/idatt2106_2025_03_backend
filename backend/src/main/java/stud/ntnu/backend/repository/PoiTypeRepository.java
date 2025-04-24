package stud.ntnu.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.PoiType;

/**
 * Repository interface for PoiType entity operations.
 */
@Repository
public interface PoiTypeRepository extends JpaRepository<PoiType, Integer> {
    // Basic CRUD operations are provided by JpaRepository
    // Custom query methods can be added as needed
}