package stud.ntnu.backend.repository.map;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stud.ntnu.backend.model.map.MeetingPlace;

/**
 * Repository interface for managing MeetingPlace entities in the database. Provides methods for
 * querying and managing meeting places. Extends JpaRepository to inherit basic CRUD operations.
 */
@Repository
public interface MeetingPlaceRepository extends JpaRepository<MeetingPlace, Integer> {

  /**
   * Retrieves all meeting places with a specific status.
   *
   * @param status The status to filter meeting places by
   * @return List of meeting places matching the specified status
   */
  List<MeetingPlace> findByStatus(String status);
}