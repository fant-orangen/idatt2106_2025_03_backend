package stud.ntnu.backend.repository.map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.map.CrisisEventChange;

/**
 * Repository interface for CrisisEventChange entity operations.
 */
@Repository
public interface CrisisEventChangeRepository extends JpaRepository<CrisisEventChange, Integer> {

    /**
     * Find all changes for a specific crisis event with pagination.
     *
     * @param crisisEventId the ID of the crisis event
     * @param pageable      pagination information
     * @return a page of crisis event changes
     */
    Page<CrisisEventChange> findByCrisisEventIdOrderByCreatedAtDesc(Integer crisisEventId, Pageable pageable);
}
