package stud.ntnu.backend.dto.news;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stud.ntnu.backend.model.news.NewsArticle;
import stud.ntnu.backend.model.news.NewsArticle.ArticleStatus;

/**
 * Data Transfer Object (DTO) representing the response data for a news article.
 * <p>
 * This DTO contains all the necessary information about a news article that needs to be returned to
 * the client, including its metadata, content, and relationships with other entities.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewsArticleResponseDTO {

  /**
   * The unique identifier of the news article.
   */
  private Long id;

  /**
   * The title of the news article.
   */
  private String title;

  /**
   * The main content of the news article.
   */
  private String content;

  /**
   * The date and time when the article was published.
   */
  private LocalDateTime publishedAt;

  /**
   * The unique identifier of the user who created the article.
   */
  private Integer createdById;

  /**
   * The name of the user who created the article.
   */
  private String createdByName;

  /**
   * The unique identifier of the associated crisis event.
   */
  private Integer crisisEventId;

  /**
   * The name of the associated crisis event.
   */
  private String crisisEventName;

  /**
   * The date and time when the article was created.
   */
  private LocalDateTime createdAt;

  /**
   * The date and time when the article was last updated.
   */
  private LocalDateTime updatedAt;

  /**
   * The current publication status of the article.
   */
  private ArticleStatus status;

  /**
   * Converts a NewsArticle entity to a NewsArticleResponseDTO.
   * <p>
   * This method maps all relevant fields from the NewsArticle entity to the DTO, including nested
   * information about the creator and associated crisis event.
   *
   * @param newsArticle the news article entity to convert
   * @return a new NewsArticleResponseDTO containing the mapped data
   */
  public static NewsArticleResponseDTO fromEntity(NewsArticle newsArticle) {
    return new NewsArticleResponseDTO(
        newsArticle.getId(),
        newsArticle.getTitle(),
        newsArticle.getContent(),
        newsArticle.getPublishedAt(),
        newsArticle.getCreatedBy().getId(),
        newsArticle.getCreatedBy().getName(),
        newsArticle.getCrisisEvent().getId(),
        newsArticle.getCrisisEvent().getName(),
        newsArticle.getCreatedAt(),
        newsArticle.getUpdatedAt(),
        newsArticle.getStatus()
    );
  }
}
