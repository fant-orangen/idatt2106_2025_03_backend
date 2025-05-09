package stud.ntnu.backend.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stud.ntnu.backend.model.user.TwoFactorCode;

/**
 * Repository interface for managing two-factor authentication codes in the database. Extends
 * JpaRepository to provide basic CRUD operations for TwoFactorCode entities.
 */
@Repository
public interface TwoFactorCodeRepository extends JpaRepository<TwoFactorCode, Integer> {

  /**
   * Finds a two-factor authentication code by email address.
   *
   * @param email The email address to search for
   * @return An Optional containing the found two-factor code, or empty if none exists
   */
  Optional<TwoFactorCode> findByEmail(String email);

  /**
   * Deletes all two-factor authentication codes associated with a specific email address.
   *
   * @param email The email address whose two-factor codes should be deleted
   */
  void deleteByEmail(String email);
}
