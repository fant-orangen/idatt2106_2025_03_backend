package stud.ntnu.backend.model.group;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

import stud.ntnu.backend.model.household.Household;

/**
 * Represents a group invitation entity in the system. This entity tracks invitations sent to
 * households for joining groups. It maintains the state of the invitation including its creation,
 * expiration, acceptance, and decline timestamps.
 */
@Entity
@Table(name = "group_invitations")
@Getter
@Setter
@NoArgsConstructor
public class GroupInvitation {

  /**
   * Unique identifier for the group invitation. Automatically generated using an identity
   * strategy.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /**
   * The group that the household is being invited to join. This is a required field and cannot be
   * null.
   */
  @ManyToOne
  @JoinColumn(name = "group_id", nullable = false)
  private Group group;

  /**
   * The email address of the user who sent the invitation. This is a required field and cannot be
   * null.
   */
  @Column(name = "inviter_email", nullable = false)
  private String inviterEmail;

  /**
   * The household that is being invited to join the group. This is a required field and cannot be
   * null.
   */
  @ManyToOne
  @JoinColumn(name = "invited_household_id", nullable = false)
  private Household invitedHousehold;

  /**
   * The timestamp when the invitation expires. After this time, the invitation can no longer be
   * accepted. This is a required field and cannot be null.
   */
  @Column(name = "expires_at", nullable = false)
  private LocalDateTime expiresAt;

  /**
   * The timestamp when the invitation was accepted. Null if the invitation has not been accepted.
   */
  @Column(name = "accepted_at")
  private LocalDateTime acceptedAt;

  /**
   * The timestamp when the invitation was declined. Null if the invitation has not been declined.
   */
  @Column(name = "declined_at")
  private LocalDateTime declinedAt;

  /**
   * The timestamp when the invitation was created. This field is automatically set and cannot be
   * updated after creation.
   */
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /**
   * Sets the creation timestamp to the current time before persisting the entity. This method is
   * automatically called by JPA before the entity is persisted.
   */
  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }

  /**
   * Creates a new group invitation with the specified parameters.
   *
   * @param group            The group that the household is being invited to join
   * @param inviterEmail     The email address of the user sending the invitation
   * @param invitedHousehold The household that is being invited
   * @param expiresAt        The timestamp when the invitation will expire
   */
  public GroupInvitation(Group group, String inviterEmail, Household invitedHousehold,
      LocalDateTime expiresAt) {
    this.group = group;
    this.inviterEmail = inviterEmail;
    this.invitedHousehold = invitedHousehold;
    this.expiresAt = expiresAt;
  }

  /**
   * Checks if the invitation is currently pending. An invitation is considered pending if it has
   * not been accepted or declined, and has not expired.
   *
   * @return true if the invitation is pending, false otherwise
   */
  public boolean isPending() {
    return acceptedAt == null && declinedAt == null && expiresAt.isAfter(LocalDateTime.now());
  }

  /**
   * Accepts the invitation if it is currently pending. Sets the acceptedAt timestamp to the current
   * time. This method has no effect if the invitation is not pending.
   */
  public void accept() {
    if (isPending()) {
      this.acceptedAt = LocalDateTime.now();
    }
  }

  /**
   * Declines the invitation if it is currently pending. Sets the declinedAt timestamp to the
   * current time. This method has no effect if the invitation is not pending.
   */
  public void decline() {
    if (isPending()) {
      this.declinedAt = LocalDateTime.now();
    }
  }
}