package stud.ntnu.backend.repository.map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.map.CrisisEventChange;

/**
 * Repository interface for managing CrisisEventChange entities. Provides methods to interact with
 * the database for crisis event change operations. Extends JpaRepository to inherit basic CRUD
 * operations.
 */
@Repository
public interface CrisisEventChangeRepository extends JpaRepository<CrisisEventChange, Integer> {

  /**
   * Retrieves all changes associated with a specific crisis event, ordered by creation date in
   * descending order. Results are paginated for efficient data retrieval.
   *
   * @param crisisEventId the unique identifier of the crisis event
   * @param pageable      the pagination parameters including page size and number
   * @return a {@link Page} containing the crisis event changes, sorted by creation date descending
   */
  Page<CrisisEventChange> findByCrisisEventIdOrderByCreatedAtDesc(Integer crisisEventId,
      Pageable pageable);
}
