package stud.ntnu.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

  // Set createdAt before persist
  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }

  // Enum for token type
  public enum TokenType {
    VERIFICATION, RESET
  }

  public EmailToken(User user, String token, TokenType type, LocalDateTime expiresAt) {
    this.user = user;
    this.token = token;
    this.type = type;
    this.expiresAt = expiresAt;
  }
}
