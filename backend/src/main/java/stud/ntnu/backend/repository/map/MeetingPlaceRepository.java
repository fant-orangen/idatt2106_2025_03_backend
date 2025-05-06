package stud.ntnu.backend.repository.map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.map.MeetingPlace;

import java.util.List;

/**
 * Repository interface for MeetingPlace entity operations.
 */
@Repository
public interface MeetingPlaceRepository extends JpaRepository<MeetingPlace, Integer> {
    List<MeetingPlace> findByStatus(String status);
}