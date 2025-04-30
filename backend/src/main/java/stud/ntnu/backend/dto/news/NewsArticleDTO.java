package stud.ntnu.backend.dto.news;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import stud.ntnu.backend.model.news.NewsArticle.ArticleStatus;

@Getter
@Setter
public class NewsArticleDTO {

  // Getters and setters
  @NotBlank(message = "Title is required")
  private String title;

  @NotBlank(message = "Content is required")
  private String content;

  @NotNull(message = "Crisis event ID is required")
  private Integer crisisEventId;

  // Status is optional in the DTO - will default to 'published' if not provided
  private ArticleStatus status;

}