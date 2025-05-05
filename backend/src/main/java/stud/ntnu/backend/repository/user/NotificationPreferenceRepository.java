package stud.ntnu.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import stud.ntnu.backend.model.user.NotificationPreference;
import stud.ntnu.backend.model.user.Notification;
import stud.ntnu.backend.model.user.User;
import java.util.Optional;

public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, Integer> {
    Optional<NotificationPreference> findByUserAndPreferenceType(User user, Notification.PreferenceType preferenceType);
} 