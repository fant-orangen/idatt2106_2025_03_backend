package stud.ntnu.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.map.MeetingPlace;

/**
 * Repository interface for MeetingPlace entity operations.
 */
@Repository
public interface MeetingPlaceRepository extends JpaRepository<MeetingPlace, Integer> {
    // Basic CRUD operations are provided by JpaRepository
    // Custom query methods can be added as needed
}