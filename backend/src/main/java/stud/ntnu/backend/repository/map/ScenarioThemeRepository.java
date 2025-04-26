package stud.ntnu.backend.repository.map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.map.ScenarioTheme;

/**
 * Repository interface for ScenarioTheme entity operations.
 */
@Repository
public interface ScenarioThemeRepository extends JpaRepository<ScenarioTheme, Integer> {
    // Basic CRUD operations are provided by JpaRepository
    // Custom query methods can be added as needed
}