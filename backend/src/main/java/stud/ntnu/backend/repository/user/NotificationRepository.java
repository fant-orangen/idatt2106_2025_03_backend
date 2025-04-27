package stud.ntnu.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.user.Notification;
import stud.ntnu.backend.model.user.User;

import java.util.List;

/**
 * Repository interface for Notification entity operations.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    
    /**
     * Find all notifications for a specific user.
     *
     * @param user the user
     * @return a list of notifications
     */
    List<Notification> findByUser(User user);
    
    /**
     * Find all notifications for a specific user ID.
     *
     * @param userId the user ID
     * @return a list of notifications
     */
    List<Notification> findByUserId(Integer userId);
    
    /**
     * Find all unread notifications for a specific user.
     *
     * @param user the user
     * @return a list of unread notifications
     */
    List<Notification> findByUserAndReadAtIsNull(User user);
}
