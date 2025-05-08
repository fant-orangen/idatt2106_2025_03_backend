package stud.ntnu.backend.dto.news;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import stud.ntnu.backend.model.news.NewsArticle.ArticleStatus;

/**
 * Data Transfer Object (DTO) representing a news article.
 * <p>
 * This DTO is used to transfer news article data between layers of the application.
 * It contains the essential information needed to create or update a news article,
 * including its title, content, associated crisis event, and publication status.
 */
@Getter
@Setter
public class NewsArticleDTO {

    /**
     * The title of the news article.
     * This field is required and cannot be blank.
     */
    @NotBlank(message = "Title is required")
    private String title;

    /**
     * The main content of the news article.
     * This field is required and cannot be blank.
     */
    @NotBlank(message = "Content is required")
    private String content;

    /**
     * The unique identifier of the associated crisis event.
     * This field is required and cannot be null.
     */
    @NotNull(message = "Crisis event ID is required")
    private Integer crisisEventId;

    /**
     * The publication status of the news article.
     * This field is optional and will default to 'published' if not provided.
     */
    private ArticleStatus status;
}