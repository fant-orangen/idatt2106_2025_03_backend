package stud.ntnu.backend.dto.news;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import stud.ntnu.backend.model.news.NewsArticle.ArticleStatus;

/**
 * Data Transfer Object (DTO) for updating news articles. This class contains the fields that can be
 * modified when updating an existing news article.
 */
@Getter
@Setter
public class UpdateNewsArticleDTO {

  /**
   * The title of the news article. Must be between 1 and 255 characters.
   */
  @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
  private String title;

  /**
   * The content of the news article. Must not be empty.
   */
  @Size(min = 1, message = "Content must not be empty")
  private String content;

  /**
   * The current status of the news article. Represents whether the article is published, draft, or
   * archived.
   */
  private ArticleStatus status;
}