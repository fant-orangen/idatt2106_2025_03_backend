package stud.ntnu.backend.model.user;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import stud.ntnu.backend.model.group.Group;
import stud.ntnu.backend.model.household.Household;

/**
 * Entity class representing a user in the system. This class stores all user-related information
 * including personal details, authentication data, and preferences.
 */
@Entity
@Table(name = "users")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Getter
@Setter
@NoArgsConstructor
public class User {

  /**
   * Unique identifier for the user.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /**
   * User's email address. Must be unique.
   */
  @Column(name = "email", nullable = false, unique = true)
  private String email;

  /**
   * Hashed version of the user's password.
   */
  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  /**
   * User's phone number.
   */
  @Column(name = "phone_number", nullable = false)
  private String phoneNumber;

  /**
   * User's role in the system.
   */
  @ManyToOne
  @JoinColumn(name = "role_id", nullable = false)
  private Role role;

  /**
   * Household the user belongs to.
   */
  @ManyToOne
  @JoinColumn(name = "household_id")
  @JsonBackReference
  private Household household;

  /**
   * User's first name.
   */
  @Column(name = "first_name")
  private String firstName;

  /**
   * User's last name.
   */
  @Column(name = "last_name")
  private String lastName;

  /**
   * User's home address.
   */
  @Column(name = "home_address", columnDefinition = "TEXT")
  private String homeAddress;

  /**
   * Latitude coordinate of user's home location.
   */
  @Column(name = "home_latitude", precision = 10, scale = 7)
  private BigDecimal homeLatitude;

  /**
   * Longitude coordinate of user's home location.
   */
  @Column(name = "home_longitude", precision = 10, scale = 7)
  private BigDecimal homeLongitude;

  /**
   * Indicates whether the user has accepted privacy terms.
   */
  @Column(name = "privacy_accepted")
  private Boolean privacyAccepted;

  /**
   * Indicates whether the user's email has been verified.
   */
  @Column(name = "email_verified", nullable = false)
  private Boolean emailVerified = false;

  /**
   * Indicates whether the user has enabled two-factor authentication.
   */
  @Column(name = "is_using_2fa", nullable = false)
  private Boolean isUsing2FA = false;

  /**
   * Indicates whether the user has enabled location sharing.
   */
  @Column(name = "location_sharing_enabled", nullable = false)
  private Boolean locationSharingEnabled = false;

  /**
   * Timestamp when the user account was created.
   */
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /**
   * User's daily calorie requirement.
   */
  @Column(name = "kcal_requirement", nullable = false)
  private Integer kcalRequirement = 2000;

  /**
   * Groups created by the user.
   */
  @OneToMany(mappedBy = "createdByUser")
  @JsonManagedReference
  private List<Group> createdGroups;

  /**
   * Sets the creation timestamp before persisting the entity.
   */
  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }

  /**
   * Constructs a new User with the specified basic information.
   *
   * @param email        The user's email address
   * @param passwordHash The hashed password
   * @param phoneNumber  The user's phone number
   * @param role         The user's role
   */
  public User(String email, String passwordHash, String phoneNumber, Role role) {
    this.email = email;
    this.passwordHash = passwordHash;
    this.phoneNumber = phoneNumber;
    this.role = role;
  }

  /**
   * Gets the user's full name. For backward compatibility.
   *
   * @return The user's full name, or null if neither first nor last name is set
   */
  public String getName() {
    if (firstName != null && lastName != null) {
      return firstName + " " + lastName;
    } else if (firstName != null) {
      return firstName;
    } else if (lastName != null) {
      return lastName;
    }
    return null;
  }

  /**
   * Sets the user's first and last name from a full name string. For backward compatibility.
   *
   * @param name The full name to parse
   */
  public void setName(String name) {
    if (name != null) {
      String[] parts = name.split(" ", 2);
      this.firstName = parts[0];
      this.lastName = parts.length > 1 ? parts[1] : "";
    }
  }
}
