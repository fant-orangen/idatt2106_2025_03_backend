package stud.ntnu.backend.model.household;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "household_member")
public class EmptyHouseholdMember {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "household_id")
  private Household household;

  private String firstName;
  private String lastName;
  private String type;
  private String description;

  public EmptyHouseholdMember(String firstName, String lastName, String type, String description) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.type = type;
    this.description = description;
  }
} 