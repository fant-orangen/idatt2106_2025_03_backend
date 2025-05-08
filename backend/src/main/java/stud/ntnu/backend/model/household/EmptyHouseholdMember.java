package stud.ntnu.backend.model.household;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
 * Represents a non-user member of a household, such as a child, pet, or other dependent.
 * These members are stored in the household_member table and have their own nutritional requirements.
 * Each member is associated with a specific household and has basic information like name, type,
 * and description.
 *
 */
@Entity
@Table(name = "household_member")
@Getter
@Setter
@NoArgsConstructor
public class EmptyHouseholdMember {

    /**
     * Unique identifier for the household member.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The household this member belongs to.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "household_id", nullable = false)
    private Household household;

    /**
     * The name of the household member.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * The type of household member (e.g., "child", "pet", "other").
     */
    @Column(name = "type", nullable = false)
    private String type;

    /**
     * Optional description of the household member.
     */
    @Column(name = "description")
    private String description;

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
    private java.time.LocalDateTime createdAt;

    /**
     * Creates a new household member with basic information.
     *
     * @param name The name of the member
     * @param type The type of member
     * @param description Optional description of the member
     */
    public EmptyHouseholdMember(String name, String type, String description) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.createdAt = java.time.LocalDateTime.now();
    }

    /**
     * Creates a new household member with basic information and custom caloric requirement.
     *
     * @param name The name of the member
     * @param type The type of member
     * @param description Optional description of the member
     * @param kcalRequirement Custom daily caloric requirement in kilocalories
     */
    public EmptyHouseholdMember(String name, String type, String description, Integer kcalRequirement) {
        this(name, type, description);
        this.kcalRequirement = kcalRequirement != null ? kcalRequirement : 2000;
    }
}