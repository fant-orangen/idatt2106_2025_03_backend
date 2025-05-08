package stud.ntnu.backend.model.household;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a member of a household in the system.
 * This entity tracks information about household members including their name,
 * type, description, and nutritional requirements.
 */
@Entity
@Table(name = "household_member")
@Getter
@Setter
@NoArgsConstructor
public class HouseholdMember {

    /**
     * Unique identifier for the household member.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The household this member belongs to.
     */
    @ManyToOne
    @JoinColumn(name = "household_id", nullable = false)
    private Household household;

    /**
     * The name of the household member.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Optional description of the household member.
     */
    private String description;

    /**
     * The type of household member (e.g., "adult", "child", "pet").
     */
    @Column(nullable = false)
    private String type;

    /**
     * Daily caloric requirement in kilocalories.
     * Default value is 2000 kcal.
     */
    @Column(name = "kcal_requirement", nullable = false)
    private Integer kcalRequirement = 2000;

    /**
     * Timestamp when this member was created.
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Creates a new household member with basic information.
     *
     * @param household The household this member belongs to
     * @param name The name of the member
     * @param type The type of member
     */
    public HouseholdMember(Household household, String name, String type) {
        this.household = household;
        this.name = name;
        this.type = type;
        this.createdAt = LocalDateTime.now();
    }
}