package stud.ntnu.backend.model.user;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing an email token used for various user-related operations.
 * Tokens can be used for email verification, password reset, or safety confirmation.
 */
@Entity
@Table(name = "email_tokens")
@Getter
@Setter
@NoArgsConstructor
public class EmailToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TokenType type;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Sets the creation timestamp before persisting the entity.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * Enum representing the different types of email tokens.
     */
    public enum TokenType {
        /** Token used for email verification */
        VERIFICATION,
        /** Token used for password reset */
        RESET,
        /** Token used for safety confirmation */
        SAFETY_CONFIRMATION
    }

    /**
     * Constructs a new EmailToken with the specified parameters.
     *
     * @param user The user associated with this token
     * @param token The token string
     * @param type The type of token
     * @param expiresAt The expiration timestamp
     */
    public EmailToken(User user, String token, TokenType type, LocalDateTime expiresAt) {
        this.user = user;
        this.token = token;
        this.type = type;
        this.expiresAt = expiresAt;
    }
}
