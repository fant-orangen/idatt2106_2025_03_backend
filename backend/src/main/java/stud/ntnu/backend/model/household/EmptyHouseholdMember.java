package stud.ntnu.backend.model.household;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents an empty household member (non-user member like a child, pet, etc.)
 * These members are stored in the household_member table.
 */
@Entity
@Table(name = "household_member")
@Getter
@Setter
@NoArgsConstructor
public class EmptyHouseholdMember {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "household_id", nullable = false)
  private Household household;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "type", nullable = false)
  private String type;

  @Column(name = "description")
  private String description;

  @Column(name = "kcal_requirement", nullable = false)
  private Integer kcalRequirement = 2000;

  @Column(name = "created_at", nullable = false)
  private java.time.LocalDateTime createdAt;

  public EmptyHouseholdMember(String name, String type, String description) {
    this.name = name;
    this.type = type;
    this.description = description;
    this.createdAt = java.time.LocalDateTime.now();
  }

  public EmptyHouseholdMember(String name, String type, String description, Integer kcalRequirement) {
    this(name, type, description);
    this.kcalRequirement = kcalRequirement != null ? kcalRequirement : 2000;
  }
} 