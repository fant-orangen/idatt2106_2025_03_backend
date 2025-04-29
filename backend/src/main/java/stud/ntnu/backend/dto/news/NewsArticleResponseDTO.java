package stud.ntnu.backend.dto.news;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stud.ntnu.backend.model.news.NewsArticle;

import java.time.LocalDateTime;

/**
 * DTO for returning news article information.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewsArticleResponseDTO {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime publishedAt;
    private Integer createdById;
    private String createdByName;
    private Integer crisisEventId;
    private String crisisEventName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Converts a NewsArticle entity to a NewsArticleResponseDTO.
     *
     * @param newsArticle the news article entity
     * @return the news article response DTO
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
                newsArticle.getUpdatedAt()
        );
    }
}
