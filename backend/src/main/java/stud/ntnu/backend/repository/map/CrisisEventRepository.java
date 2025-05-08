package stud.ntnu.backend.repository.map;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import stud.ntnu.backend.model.map.CrisisEvent;
import stud.ntnu.backend.model.map.CrisisEvent.Severity;
import stud.ntnu.backend.model.map.ScenarioTheme;
import stud.ntnu.backend.model.user.User;

/**
 * Repository interface for managing CrisisEvent entities in the database.
 * Provides methods for querying, updating, and managing crisis events.
 */
@Repository
public interface CrisisEventRepository extends JpaRepository<CrisisEvent, Integer> {

  /**
   * Retrieves all active crisis events.
   *
   * @return List of active crisis events
   */
  List<CrisisEvent> findByActiveTrue();

  /**
   * Retrieves all inactive crisis events.
   *
   * @return List of inactive crisis events
   */
  List<CrisisEvent> findByActiveFalse();

  /**
   * Retrieves crisis events by their severity level.
   *
   * @param severity The severity level to filter by
   * @return List of crisis events matching the severity
   */
  List<CrisisEvent> findBySeverity(Severity severity);

  /**
   * Retrieves crisis events created by a specific user.
   *
   * @param user The user who created the crisis events
   * @return List of crisis events created by the specified user
   */
  List<CrisisEvent> findByCreatedByUser(User user);

  /**
   * Retrieves active crisis events filtered by severity.
   *
   * @param severity The severity level to filter by
   * @return List of active crisis events matching the severity
   */
  List<CrisisEvent> findByActiveTrueAndSeverity(Severity severity);

  /**
   * Retrieves crisis events by their scenario theme.
   *
   * @param scenarioTheme The scenario theme to filter by
   * @return List of crisis events matching the scenario theme
   */
  List<CrisisEvent> findByScenarioTheme(ScenarioTheme scenarioTheme);

  /**
   * Retrieves active crisis events filtered by scenario theme.
   *
   * @param scenarioTheme The scenario theme to filter by
   * @return List of active crisis events matching the scenario theme
   */
  List<CrisisEvent> findByActiveTrueAndScenarioTheme(ScenarioTheme scenarioTheme);

  /**
   * Updates the fields of a crisis event.
   * Updates include name, description, severity, location coordinates, radius, and timestamp.
   *
   * @param id The ID of the crisis event to update
   * @param name The new name of the crisis event
   * @param description The new description of the crisis event
   * @param severity The new severity level
   * @param latitude The new latitude coordinate
   * @param longitude The new longitude coordinate
   * @param radius The new radius value
   */
  @Modifying
  @Query("UPDATE CrisisEvent c SET c.name = :name, c.description = :description, " +
      "c.severity = :severity, c.epicenterLatitude = :latitude, " +
      "c.epicenterLongitude = :longitude, c.radius = :radius, c.updatedAt = CURRENT_TIMESTAMP WHERE c.id = :id")
  void updateCrisisEvent(
      @Param("id") Integer id,
      @Param("name") String name,
      @Param("description") String description,
      @Param("severity") Severity severity,
      @Param("latitude") BigDecimal latitude,
      @Param("longitude") BigDecimal longitude,
      @Param("radius") BigDecimal radius
  );

  /**
   * Deactivates a crisis event by setting its active status to false.
   *
   * @param id The ID of the crisis event to deactivate
   */
  @Modifying
  @Query("UPDATE CrisisEvent c SET c.active = false WHERE c.id = :id")
  void deactivateCrisisEvent(@Param("id") Integer id);

  /**
   * Retrieves a paginated list of active crisis events.
   *
   * @param pageable Pagination information
   * @return Page of active crisis events
   */
  Page<CrisisEvent> findByActiveTrue(Pageable pageable);

  /**
   * Retrieves a paginated list of inactive crisis events.
   *
   * @param pageable Pagination information
   * @return Page of inactive crisis events
   */
  Page<CrisisEvent> findByActiveFalse(Pageable pageable);
}