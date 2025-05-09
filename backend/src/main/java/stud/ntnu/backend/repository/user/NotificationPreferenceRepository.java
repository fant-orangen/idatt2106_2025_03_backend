package stud.ntnu.backend.repository.user;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import stud.ntnu.backend.model.user.Notification;
import stud.ntnu.backend.model.user.NotificationPreference;
import stud.ntnu.backend.model.user.User;

/**
 * Repository interface for managing notification preferences. Extends JpaRepository to provide
 * basic CRUD operations for NotificationPreference entities.
 */
public interface NotificationPreferenceRepository extends
    JpaRepository<NotificationPreference, Integer> {

  /**
   * Finds a notification preference for a specific user and preference type.
   *
   * @param user           The user whose notification preference to find
   * @param preferenceType The type of notification preference to find
   * @return An Optional containing the found notification preference, or empty if none exists
   */
  Optional<NotificationPreference> findByUserAndPreferenceType(User user,
      Notification.PreferenceType preferenceType);

  /**
   * Gets the notification preferences for a specific user.
   */
    List<NotificationPreference> findByUser(User user);
}