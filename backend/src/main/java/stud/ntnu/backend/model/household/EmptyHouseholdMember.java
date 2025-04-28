package stud.ntnu.backend.model.household;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an empty household member (non-user member like a child, pet, etc.)
 * These members are stored in the household_member table.
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "household_member")
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

  @Column(name = "created_at", nullable = false)
  private java.time.LocalDateTime createdAt;

  public EmptyHouseholdMember(String name, String type, String description) {
    this.name = name;
    this.type = type;
    this.description = description;
    this.createdAt = java.time.LocalDateTime.now();
  }
} 