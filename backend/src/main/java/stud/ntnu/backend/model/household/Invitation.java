package stud.ntnu.backend.model.household;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.model.group.Group;

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

  @Column(name = "declined_at")
  private LocalDateTime declinedAt;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  // Enum for invitation status
  public enum Status {
    PENDING, ACCEPTED, DECLINED, EXPIRED
  }

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

  /**
   * Constructor for household invitations.
   */
  public Invitation(User inviterUser, String inviteeEmail, Household household, String token, LocalDateTime expiresAt) {
    this.inviterUser = inviterUser;
    this.inviteeEmail = inviteeEmail;
    this.household = household;
    this.token = token;
    this.expiresAt = expiresAt;
  }

  /**
   * Get the current status of the invitation.
   *
   * @return the status of the invitation
   */
  public Status getStatus() {
    if (acceptedAt != null) {
      return Status.ACCEPTED;
    } else if (declinedAt != null) {
      return Status.DECLINED;
    } else if (expiresAt.isBefore(LocalDateTime.now())) {
      return Status.EXPIRED;
    } else {
      return Status.PENDING;
    }
  }

  /**
   * Check if the invitation is pending (not accepted, declined, or expired).
   *
   * @return true if the invitation is pending, false otherwise
   */
  public boolean isPending() {
    return getStatus() == Status.PENDING;
  }

  /**
   * Accept the invitation.
   */
  public void accept() {
    if (isPending()) {
      this.acceptedAt = LocalDateTime.now();
    }
  }

  /**
   * Decline the invitation.
   */
  public void decline() {
    if (isPending()) {
      this.declinedAt = LocalDateTime.now();
    }
  }
}
