package stud.ntnu.backend.model.household;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "household_member")
@Getter
@Setter
@NoArgsConstructor
public class HouseholdMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "household_id", nullable = false)
    private Household household;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private String type;

    @Column(name = "kcal_requirement", nullable = false)
    private Integer kcalRequirement = 2000;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public HouseholdMember(Household household, String name, String type) {
        this.household = household;
        this.name = name;
        this.type = type;
        this.createdAt = LocalDateTime.now();
    }
} 