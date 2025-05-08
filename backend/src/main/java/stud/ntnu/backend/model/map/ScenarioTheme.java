package stud.ntnu.backend.model.map;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import stud.ntnu.backend.model.user.User;

/**
 * Represents a scenario theme in the system.
 * This entity stores information about different scenario themes including their descriptions,
 * lifecycle stages (before, under, after), and associated metadata.
 */
@Entity
@Table(name = "scenario_themes")
@Getter
@Setter
@NoArgsConstructor
public class ScenarioTheme {

    /**
     * Unique identifier for the scenario theme.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The name of the scenario theme.
     * Must be unique across all themes.
     */
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    /**
     * Detailed description of the scenario theme.
     * Stored as TEXT in the database.
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Description of the situation before the scenario.
     * Stored as TEXT in the database.
     */
    @Column(name = "before", columnDefinition = "TEXT")
    private String before;

    /**
     * Description of the situation during the scenario.
     * Stored as TEXT in the database.
     */
    @Column(name = "under", columnDefinition = "TEXT")
    private String under;

    /**
     * Description of the situation after the scenario.
     * Stored as TEXT in the database.
     */
    @Column(name = "after", columnDefinition = "TEXT")
    private String after;

    /**
     * The user who created this scenario theme.
     */
    @ManyToOne
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdByUser;

    /**
     * The timestamp when this scenario theme was created.
     * This field cannot be updated after creation.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * The timestamp when this scenario theme was last updated.
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Current status of the scenario theme.
     * Default value is "active".
     */
    @Column(name = "status", nullable = false)
    private String status = "active";

    /**
     * Sets the creation and update timestamps before persisting the entity.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Updates the last modified timestamp before any update operation.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Constructs a new scenario theme with the specified name and creator.
     *
     * @param name The name of the scenario theme
     * @param createdByUser The user creating this scenario theme
     */
    public ScenarioTheme(String name, User createdByUser) {
        this.name = name;
        this.createdByUser = createdByUser;
    }

    /**
     * Constructs a new scenario theme with all specified parameters.
     *
     * @param name The name of the scenario theme
     * @param description The detailed description of the theme
     * @param before The situation before the scenario
     * @param under The situation during the scenario
     * @param after The situation after the scenario
     * @param createdByUser The user creating this scenario theme
     */
    public ScenarioTheme(String name, String description, String before, String under, String after, User createdByUser) {
        this.name = name;
        this.description = description;
        this.before = before;
        this.under = under;
        this.after = after;
        this.createdByUser = createdByUser;
    }
}