package stud.ntnu.backend.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

/**
 * Entity representing a two-factor authentication code for a user. This code is used to verify the
 * user's identity during the login process. The code is associated with a user's email and has an
 * expiration time.
 */
@Setter
@Getter
@Entity
@Table(name = "two_factor_codes")
public class TwoFactorCode {

  /**
   * Unique identifier for the two-factor code.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /**
   * Email address associated with this two-factor code. Must be unique and cannot be null.
   */
  @Column(name = "email", nullable = false, unique = true)
  private String email;

  /**
   * The actual two-factor authentication code. Cannot be null.
   */
  @Column(name = "code", nullable = false)
  private Integer code;

  /**
   * The expiration date and time of this two-factor code. Cannot be null.
   */
  @Column(name = "expires_at", nullable = false)
  private LocalDateTime expiresAt;
}
