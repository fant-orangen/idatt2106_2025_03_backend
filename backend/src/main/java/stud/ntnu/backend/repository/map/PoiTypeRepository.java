package stud.ntnu.backend.repository.map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.map.PoiType;

/**
 * Repository interface for managing PoiType entities in the database.
 * Provides basic CRUD operations through JpaRepository and allows for custom query methods.
 * 
 * @see PoiType
 * @see JpaRepository
 */
@Repository
public interface PoiTypeRepository extends JpaRepository<PoiType, Integer> {
}