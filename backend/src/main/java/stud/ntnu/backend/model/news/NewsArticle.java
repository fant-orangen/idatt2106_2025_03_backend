package stud.ntnu.backend.model.news;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import stud.ntnu.backend.model.map.CrisisEvent;
import stud.ntnu.backend.model.user.User;

/**
 * Represents a news article in the system.
 * This entity stores information about news articles including their content,
 * publication status, and associated metadata.
 */
@Setter
@Getter
@Entity
@Table(name = "news_articles")
public class NewsArticle {

    /**
     * Unique identifier for the news article.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The title of the news article.
     */
    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;

    /**
     * The main content of the news article.
     */
    @NotBlank(message = "Content is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * The date and time when the article was published.
     */
    @NotNull(message = "Published date is required")
    @Column(name = "published_at", nullable = false)
    private LocalDateTime publishedAt;

    /**
     * The user who created the article.
     */
    @NotNull(message = "Created by user is required")
    @ManyToOne
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    /**
     * The crisis event associated with this news article.
     */
    @NotNull(message = "Crisis event is required")
    @ManyToOne
    @JoinColumn(name = "crisis_event_id", nullable = false)
    private CrisisEvent crisisEvent;

    /**
     * The current status of the article.
     */
    @NotNull(message = "Status is required")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ArticleStatus status;

    /**
     * The date and time when the article was created.
     */
    @NotNull(message = "Created date is required")
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * The date and time when the article was last updated.
     */
    @NotNull(message = "Updated date is required")
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Enum representing the possible statuses of a news article.
     * DRAFT: Article is in progress and not yet published
     * PUBLISHED: Article is live and visible to users
     * ARCHIVED: Article is no longer actively displayed
     */
    public enum ArticleStatus {
        draft, published, archived
    }
}