package stud.ntnu.backend.repository.map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.map.CrisisEvent;
import stud.ntnu.backend.model.map.CrisisEvent.Severity;
import stud.ntnu.backend.model.user.User;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository interface for CrisisEvent entity operations.
 */
@Repository
public interface CrisisEventRepository extends JpaRepository<CrisisEvent, Integer> {

  // Find active crisis events
  List<CrisisEvent> findByActiveTrue();

  // Find crisis events by severity
  List<CrisisEvent> findBySeverity(Severity severity);

  // Find crisis events created by a specific user
  List<CrisisEvent> findByCreatedByUser(User user);

  // Find active crisis events by severity
  List<CrisisEvent> findByActiveTrueAndSeverity(Severity severity);

  // Update crisis event fields directly using a query (excluding start time)
  @Modifying
  @Query("UPDATE CrisisEvent c SET c.name = :name, c.description = :description, " +
      "c.severity = :severity, c.epicenterLatitude = :latitude, " +
      "c.epicenterLongitude = :longitude, c.radius = :radius WHERE c.id = :id")
  void updateCrisisEvent(
      @Param("id") Integer id,
      @Param("name") String name,
      @Param("description") String description,
      @Param("severity") Severity severity,
      @Param("latitude") BigDecimal latitude,
      @Param("longitude") BigDecimal longitude,
      @Param("radius") BigDecimal radius
  );

  // Set a crisis event as inactive
  @Modifying
  @Query("UPDATE CrisisEvent c SET c.active = false WHERE c.id = :id")
  void deactivateCrisisEvent(@Param("id") Integer id);
}