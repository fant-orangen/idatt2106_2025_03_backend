package stud.ntnu.backend.dto.household;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for returning household information.
 */
public class HouseholdDto {

  private Integer id;
  private String name;
  private String address;
  private Integer populationCount;
  private BigDecimal latitude;
  private BigDecimal longitude;
  private List<HouseholdMemberDto> members;

  // Default constructor
  public HouseholdDto() {
  }

  // Constructor with all fields
  public HouseholdDto(Integer id, String name, String address, Integer populationCount,
      BigDecimal latitude, BigDecimal longitude, List<HouseholdMemberDto> members) {
    this.id = id;
    this.name = name;
    this.address = address;
    this.populationCount = populationCount;
    this.latitude = latitude;
    this.longitude = longitude;
    this.members = members;
  }

  // Getters and setters
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public Integer getPopulationCount() {
    return populationCount;
  }

  public void setPopulationCount(Integer populationCount) {
    this.populationCount = populationCount;
  }

  public BigDecimal getLatitude() {
    return latitude;
  }

  public void setLatitude(BigDecimal latitude) {
    this.latitude = latitude;
  }

  public BigDecimal getLongitude() {
    return longitude;
  }

  public void setLongitude(BigDecimal longitude) {
    this.longitude = longitude;
  }

  public List<HouseholdMemberDto> getMembers() {
    return members;
  }

  public void setMembers(List<HouseholdMemberDto> members) {
    this.members = members;
  }

  /**
   * DTO for a household member.
   */
  public static class HouseholdMemberDto {

    private Integer id;
    private String email;
    private String firstName;
    private String lastName;

    // Default constructor
    public HouseholdMemberDto() {
    }

    // Constructor with all fields
    public HouseholdMemberDto(Integer id, String email, String firstName, String lastName) {
      this.id = id;
      this.email = email;
      this.firstName = firstName;
      this.lastName = lastName;
    }

    // Getters and setters
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

    public String getFirstName() {
      return firstName;
    }

    public void setFirstName(String firstName) {
      this.firstName = firstName;
    }

    public String getLastName() {
      return lastName;
    }

    public void setLastName(String lastName) {
      this.lastName = lastName;
    }
  }
}