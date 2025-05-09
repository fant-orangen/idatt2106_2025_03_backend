package stud.ntnu.backend.dto.auth;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import stud.ntnu.backend.validation.CoordinatesPair;

/**
 * Data Transfer Object for user registration requests. Extends AuthRequestDto to include additional
 * user information required for registration.
 */
@Setter
@Getter
@CoordinatesPair(latitudeField = "homeLatitude", longitudeField = "homeLongitude",
    message = "Both latitude and longitude must be provided together")
public class RegisterRequestDto extends AuthRequestDto {

  /**
   * User's first name. Required field.
   */
  @NotBlank(message = "First name is required")
  private String firstName;

  /**
   * User's last name. Required field.
   */
  @NotBlank(message = "Last name is required")
  private String lastName;

  /**
   * User's phone number. Required field.
   */
  @NotBlank(message = "Phone number is required")
  private String phoneNumber;

  /**
   * Indicates whether the user has accepted the privacy policy. Must be true for registration.
   */
  @AssertTrue
  private Boolean privacyPolicyAccepted;

  /**
   * User's home address. Optional field.
   */
  private String homeAddress;

  /**
   * User's home location latitude. Must be provided together with longitude if either is present.
   */
  private BigDecimal homeLatitude;

  /**
   * User's home location longitude. Must be provided together with latitude if either is present.
   */
  private BigDecimal homeLongitude;

  /**
   * Default constructor.
   */
  public RegisterRequestDto() {
    super();
  }

  /**
   * Constructor with all fields.
   *
   * @param email                 User's email address
   * @param password              User's password
   * @param recaptchaToken        reCAPTCHA verification token
   * @param firstName             User's first name
   * @param lastName              User's last name
   * @param phoneNumber           User's phone number
   * @param homeAddress           User's home address
   * @param homeLatitude          User's home location latitude
   * @param homeLongitude         User's home location longitude
   * @param privacyPolicyAccepted Whether user accepted privacy policy
   */
  public RegisterRequestDto(String email, String password, String recaptchaToken,
      String firstName, String lastName,
      String phoneNumber, String homeAddress,
      BigDecimal homeLatitude, BigDecimal homeLongitude, Boolean privacyPolicyAccepted) {
    super(email, password, recaptchaToken);
    this.firstName = firstName;
    this.lastName = lastName;
    this.phoneNumber = phoneNumber;
    this.homeAddress = homeAddress;
    this.homeLatitude = homeLatitude;
    this.homeLongitude = homeLongitude;
    this.privacyPolicyAccepted = privacyPolicyAccepted;
  }

  /**
   * Constructor with combined name for backward compatibility. Splits the provided name into first
   * and last name components.
   *
   * @param email                 User's email address
   * @param password              User's password
   * @param recaptchaToken        reCAPTCHA verification token
   * @param name                  Combined first and last name
   * @param phoneNumber           User's phone number
   * @param homeAddress           User's home address
   * @param homeLatitude          User's home location latitude
   * @param homeLongitude         User's home location longitude
   * @param privacyPolicyAccepted Whether user accepted privacy policy
   */
  public RegisterRequestDto(String email, String password, String recaptchaToken,
      String name,
      String phoneNumber, String homeAddress,
      BigDecimal homeLatitude, BigDecimal homeLongitude, Boolean privacyPolicyAccepted) {
    super(email, password, recaptchaToken);
    if (name != null) {
      String[] parts = name.split(" ", 2);
      this.firstName = parts[0];
      this.lastName = parts.length > 1 ? parts[1] : "";
    }
    this.phoneNumber = phoneNumber;
    this.homeAddress = homeAddress;
    this.homeLatitude = homeLatitude;
    this.homeLongitude = homeLongitude;
    this.privacyPolicyAccepted = privacyPolicyAccepted;
  }

  /**
   * Sets the user's name by splitting the provided name into first and last name components. For
   * backward compatibility.
   *
   * @param name Combined first and last name
   */
  public void setName(String name) {
    if (name != null) {
      String[] parts = name.split(" ", 2);
      this.firstName = parts[0];
      this.lastName = parts.length > 1 ? parts[1] : "";
    }
  }

  /**
   * Gets the user's full name by combining first and last name. For backward compatibility.
   *
   * @return Combined first and last name, or null if neither is set
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
}