package stud.ntnu.backend.model.household;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

import stud.ntnu.backend.model.user.User;

/**
 * Represents an administrator of a household in the system.
 * This entity maintains the relationship between a user and their administrative role
 * within a specific household.
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "household_admins")
public class HouseholdAdmin {

    /**
     * The unique identifier for the household admin record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The user who is an administrator of the household.
     * Each user can only be an admin of one household at a time.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    private User user;

    /**
     * The household that this user administers.
     * Multiple admins can be associated with a single household.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "household_id", referencedColumnName = "id")
    private Household household;

    /**
     * The timestamp when this admin record was created.
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Creates a new household admin record.
     *
     * @param user The user to be assigned as an admin
     * @param household The household the user will administer
     */
    public HouseholdAdmin(User user, Household household) {
        this.user = user;
        this.household = household;
        this.createdAt = LocalDateTime.now();
    }
}