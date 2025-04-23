package stud.ntnu.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "invitations")
@Getter
@Setter
@NoArgsConstructor
public class Invitation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "inviter_user_id", nullable = false)
  private User inviterUser;

  @Column(name = "invitee_email", nullable = false)
  private String inviteeEmail;

  @ManyToOne
  @JoinColumn(name = "household_id")
  private Household household;

  @ManyToOne
  @JoinColumn(name = "group_id")
  private Group group;

  @Column(name = "token", nullable = false, unique = true)
  private String token;

  @Column(name = "expires_at", nullable = false)
  private LocalDateTime expiresAt;

  @Column(name = "accepted_at")
  private LocalDateTime acceptedAt;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  // Set createdAt before persist
  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }

  public Invitation(User inviterUser, String inviteeEmail, String token, LocalDateTime expiresAt) {
    this.inviterUser = inviterUser;
    this.inviteeEmail = inviteeEmail;
    this.token = token;
    this.expiresAt = expiresAt;
  }
}
