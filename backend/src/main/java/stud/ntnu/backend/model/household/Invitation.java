package stud.ntnu.backend.model.household;

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

import stud.ntnu.backend.model.group.Group;
import stud.ntnu.backend.model.user.User;

/**
 * Represents an invitation entity in the system.
 * This entity tracks invitations sent to users for joining households or groups,
 * including their status, expiration, and acceptance/decline timestamps.
 */
@Entity
@Table(name = "invitations")
@Getter
@Setter
@NoArgsConstructor
public class Invitation {

    /**
     * Unique identifier for the invitation.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The user who sent the invitation.
     */
    @ManyToOne
    @JoinColumn(name = "inviter_user_id", nullable = false)
    private User inviterUser;

    /**
     * Email address of the user being invited.
     */
    @Column(name = "invitee_email", nullable = false)
    private String inviteeEmail;

    /**
     * The household the invitation is for, if applicable.
     */
    @ManyToOne
    @JoinColumn(name = "household_id")
    private Household household;

    /**
     * The group the invitation is for, if applicable.
     */
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    /**
     * Unique token used to identify and validate the invitation.
     */
    @Column(name = "token", nullable = false, unique = true)
    private String token;

    /**
     * Timestamp when the invitation expires.
     */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /**
     * Timestamp when the invitation was accepted, if applicable.
     */
    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    /**
     * Timestamp when the invitation was declined, if applicable.
     */
    @Column(name = "declined_at")
    private LocalDateTime declinedAt;

    /**
     * Timestamp when the invitation was created.
     * Cannot be updated after creation.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Possible states of an invitation.
     */
    public enum Status {
        PENDING, ACCEPTED, DECLINED, EXPIRED
    }

    /**
     * Sets the creation timestamp to the current time before persisting the entity.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * Creates a new invitation with basic information.
     *
     * @param inviterUser The user sending the invitation
     * @param inviteeEmail The email address of the invitee
     * @param token The unique token for the invitation
     * @param expiresAt The expiration timestamp
     */
    public Invitation(User inviterUser, String inviteeEmail, String token, LocalDateTime expiresAt) {
        this.inviterUser = inviterUser;
        this.inviteeEmail = inviteeEmail;
        this.token = token;
        this.expiresAt = expiresAt;
    }

    /**
     * Creates a new household invitation.
     *
     * @param inviterUser The user sending the invitation
     * @param inviteeEmail The email address of the invitee
     * @param household The household being invited to
     * @param token The unique token for the invitation
     * @param expiresAt The expiration timestamp
     */
    public Invitation(User inviterUser, String inviteeEmail, Household household, String token, LocalDateTime expiresAt) {
        this.inviterUser = inviterUser;
        this.inviteeEmail = inviteeEmail;
        this.household = household;
        this.token = token;
        this.expiresAt = expiresAt;
    }

    /**
     * Gets the current status of the invitation.
     *
     * @return The current status of the invitation
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
     * Checks if the invitation is pending.
     *
     * @return true if the invitation is pending, false otherwise
     */
    public boolean isPending() {
        return getStatus() == Status.PENDING;
    }

    /**
     * Accepts the invitation if it is pending.
     * Sets the acceptedAt timestamp to the current time.
     */
    public void accept() {
        if (isPending()) {
            this.acceptedAt = LocalDateTime.now();
        }
    }

    /**
     * Declines the invitation if it is pending.
     * Sets the declinedAt timestamp to the current time.
     */
    public void decline() {
        if (isPending()) {
            this.declinedAt = LocalDateTime.now();
        }
    }
}
