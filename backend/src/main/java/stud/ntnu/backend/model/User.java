package stud.ntnu.backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  @ManyToOne
  @JoinColumn(name = "role_id", nullable = false)
  private Role role;

  @ManyToOne
  @JoinColumn(name = "household_id")
  private Household household;

  @Column(name = "name")
  private String name;

  @Column(name = "home_address", columnDefinition = "TEXT")
  private String homeAddress;

  @Column(name = "home_latitude", precision = 10, scale = 7)
  private BigDecimal homeLatitude;

  @Column(name = "home_longitude", precision = 10, scale = 7)
  private BigDecimal homeLongitude;

  @Column(name = "privacy_accepted_at")
  private LocalDateTime privacyAcceptedAt;

  @Column(name = "email_verified", nullable = false)
  private Boolean emailVerified = false;

  @Column(name = "location_sharing_enabled", nullable = false)
  private Boolean locationSharingEnabled = false;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @OneToMany(mappedBy = "createdByUser")
  private List<Group> createdGroups;

  // Set createdAt before persist
  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }

  // Constructors
  public User() {
  }

  public User(String email, String passwordHash, Role role) {
    this.email = email;
    this.passwordHash = passwordHash;
    this.role = role;
  }

  // Getters and Setters
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public Household getHousehold() {
    return household;
  }

  public void setHousehold(Household household) {
    this.household = household;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getHomeAddress() {
    return homeAddress;
  }

  public void setHomeAddress(String homeAddress) {
    this.homeAddress = homeAddress;
  }

  public BigDecimal getHomeLatitude() {
    return homeLatitude;
  }

  public void setHomeLatitude(BigDecimal homeLatitude) {
    this.homeLatitude = homeLatitude;
  }

  public BigDecimal getHomeLongitude() {
    return homeLongitude;
  }

  public void setHomeLongitude(BigDecimal homeLongitude) {
    this.homeLongitude = homeLongitude;
  }

  public LocalDateTime getPrivacyAcceptedAt() {
    return privacyAcceptedAt;
  }

  public void setPrivacyAcceptedAt(LocalDateTime privacyAcceptedAt) {
    this.privacyAcceptedAt = privacyAcceptedAt;
  }

  public Boolean getEmailVerified() {
    return emailVerified;
  }

  public void setEmailVerified(Boolean emailVerified) {
    this.emailVerified = emailVerified;
  }

  public Boolean getLocationSharingEnabled() {
    return locationSharingEnabled;
  }

  public void setLocationSharingEnabled(Boolean locationSharingEnabled) {
    this.locationSharingEnabled = locationSharingEnabled;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public List<Group> getCreatedGroups() {
    return createdGroups;
  }

  public void setCreatedGroups(List<Group> createdGroups) {
    this.createdGroups = createdGroups;
  }
}
