package stud.ntnu.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "email_tokens")
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

  // Constructors
  public EmailToken() {
  }

  public EmailToken(User user, String token, TokenType type, LocalDateTime expiresAt) {
    this.user = user;
    this.token = token;
    this.type = type;
    this.expiresAt = expiresAt;
  }

  // Getters and Setters
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public TokenType getType() {
    return type;
  }

  public void setType(TokenType type) {
    this.type = type;
  }

  public LocalDateTime getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(LocalDateTime expiresAt) {
    this.expiresAt = expiresAt;
  }

  public LocalDateTime getUsedAt() {
    return usedAt;
  }

  public void setUsedAt(LocalDateTime usedAt) {
    this.usedAt = usedAt;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}