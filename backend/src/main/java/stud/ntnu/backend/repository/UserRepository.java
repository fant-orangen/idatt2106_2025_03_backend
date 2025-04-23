package stud.ntnu.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.User;

import java.util.Optional;

/**
 * Repository interface for User entity operations. Provides methods to find users by email and
 * other criteria.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

  /**
   * Find a user by their email address.
   *
   * @param email the email address to search for
   * @return an Optional containing the user if found, or empty if not found
   */
  Optional<User> findByEmail(String email);

  /**
   * Check if a user exists with the given email address.
   *
   * @param email the email address to check
   * @return true if a user exists with the email, false otherwise
   */
  boolean existsByEmail(String email);
}