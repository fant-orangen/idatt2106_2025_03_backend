package stud.ntnu.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.user.UserGamificationActivity;

/**
 * Repository interface for managing user gamification activities in the database.
 * Extends JpaRepository to provide basic CRUD operations for UserGamificationActivity entities.
 * This repository handles the persistence and retrieval of user gamification-related activities
 * such as achievements, points, and progress tracking.
 */
@Repository
public interface UserGamificationActivityRepository extends JpaRepository<UserGamificationActivity, Integer> {
    // Basic CRUD operations are provided by JpaRepository
    // Custom query methods can be added as needed
}