package stud.ntnu.backend.model.news;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.model.map.CrisisEvent;

@Setter
@Getter
@Entity
@Table(name = "news_articles")
public class NewsArticle {

  // Getters and setters
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Title is required")
  @Column(nullable = false)
  private String title;

  @NotBlank(message = "Content is required")
  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @NotNull(message = "Published date is required")
  @Column(name = "published_at", nullable = false)
  private LocalDateTime publishedAt;

  @NotNull(message = "Created by user is required")
  @ManyToOne
  @JoinColumn(name = "created_by_user_id", nullable = false)
  private User createdBy;

  @NotNull(message = "Crisis event is required")
  @ManyToOne
  @JoinColumn(name = "crisis_event_id", nullable = false)
  private CrisisEvent crisisEvent;

  @NotNull(message = "Status is required")
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ArticleStatus status;

  @NotNull(message = "Created date is required")
  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @NotNull(message = "Updated date is required")
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  /**
   * Enum representing the possible statuses of a news article.
   */
  public enum ArticleStatus {
    draft, published, archived
  }

}