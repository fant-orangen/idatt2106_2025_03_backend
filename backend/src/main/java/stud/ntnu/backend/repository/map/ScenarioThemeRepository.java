package stud.ntnu.backend.repository.map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.map.ScenarioTheme;

/**
 * Repository interface for managing ScenarioTheme entities in the database.
 * Provides standard CRUD operations through JpaRepository and allows for custom query methods.
 * 
 * @see JpaRepository
 * @see ScenarioTheme
 */
@Repository
public interface ScenarioThemeRepository extends JpaRepository<ScenarioTheme, Integer> {
}