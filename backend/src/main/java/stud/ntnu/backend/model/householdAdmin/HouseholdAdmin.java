package stud.ntnu.backend.model.householdAdmin;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import stud.ntnu.backend.model.household.Household;
import stud.ntnu.backend.model.user.User;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "household_admins")
@IdClass(HouseholdAdminId.class)
public class HouseholdAdmin {
    @Id
    @Column(name = "user_id")
    private Integer userId;

    @Id
    @Column(name = "household_id")
    private Integer householdId;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "household_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Household household;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public HouseholdAdmin(User user, Household household) {
        this.userId = user.getId();
        this.householdId = household.getId();
        this.user = user;
        this.household = household;
        this.createdAt = LocalDateTime.now();
    }
} 