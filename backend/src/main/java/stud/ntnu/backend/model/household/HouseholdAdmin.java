package stud.ntnu.backend.model.household;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import stud.ntnu.backend.model.user.User;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "household_admins")
public class HouseholdAdmin {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "household_id", referencedColumnName = "id")
  private Household household;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  public HouseholdAdmin(User user, Household household) {
    this.user = user;
    this.household = household;
    this.createdAt = LocalDateTime.now();
  }
} 