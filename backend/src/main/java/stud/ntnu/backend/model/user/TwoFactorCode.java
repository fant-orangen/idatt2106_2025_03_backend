package stud.ntnu.backend.model.user;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <h2>TwoFactorCode</h2>
 *
 * <p>Entity representing a two-factor authentication code for a user.
 * This code is used to verify the user's identity during the login process.</p>
 */
@Setter
@Getter
@Entity
@Table(name = "two_factor_codes")
public class TwoFactorCode {

    // Getters and setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "code", nullable = false)
    private Integer code;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

}
