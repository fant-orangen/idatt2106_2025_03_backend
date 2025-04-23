package stud.ntnu.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "invitations")
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

  // Constructors
  public Invitation() {
  }

  public Invitation(User inviterUser, String inviteeEmail, String token, LocalDateTime expiresAt) {
    this.inviterUser = inviterUser;
    this.inviteeEmail = inviteeEmail;
    this.token = token;
    this.expiresAt = expiresAt;
  }

  // Getters and Setters
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public User getInviterUser() {
    return inviterUser;
  }

  public void setInviterUser(User inviterUser) {
    this.inviterUser = inviterUser;
  }

  public String getInviteeEmail() {
    return inviteeEmail;
  }

  public void setInviteeEmail(String inviteeEmail) {
    this.inviteeEmail = inviteeEmail;
  }

  public Household getHousehold() {
    return household;
  }

  public void setHousehold(Household household) {
    this.household = household;
  }

  public Group getGroup() {
    return group;
  }

  public void setGroup(Group group) {
    this.group = group;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public LocalDateTime getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(LocalDateTime expiresAt) {
    this.expiresAt = expiresAt;
  }

  public LocalDateTime getAcceptedAt() {
    return acceptedAt;
  }

  public void setAcceptedAt(LocalDateTime acceptedAt) {
    this.acceptedAt = acceptedAt;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}