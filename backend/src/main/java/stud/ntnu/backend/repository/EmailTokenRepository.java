package stud.ntnu.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.user.EmailToken;

import java.util.Optional;

/**
 * Repository interface for {@link EmailToken} entity operations.
 * Provides methods for standard CRUD operations inherited from JpaRepository,
 * as well as custom queries for finding tokens.
 */
@Repository
public interface EmailTokenRepository extends JpaRepository<EmailToken, Integer> {

  /**
   * Finds an EmailToken entity by its unique token string.
   * This is typically used during the email verification or password reset process
   * to locate the token record based on the token provided by the user.
   *
   * @param token The unique token string to search for. Must not be null.
   * @return an {@link Optional} containing the {@link EmailToken} if found, or an empty Optional if no token matches.
   */
  Optional<EmailToken> findByToken(String token);

}