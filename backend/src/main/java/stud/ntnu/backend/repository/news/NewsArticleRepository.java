package stud.ntnu.backend.repository.news;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.news.NewsArticle;

/**
 * Repository interface for NewsArticle entity operations.
 */
@Repository
public interface NewsArticleRepository extends JpaRepository<NewsArticle, Long> {

    /**
     * Find all news articles for a specific crisis event with pagination.
     *
     * @param crisisEventId the crisis event ID
     * @param pageable pagination information
     * @return a page of news articles
     */
    Page<NewsArticle> findByCrisisEventId(Integer crisisEventId, Pageable pageable);
}