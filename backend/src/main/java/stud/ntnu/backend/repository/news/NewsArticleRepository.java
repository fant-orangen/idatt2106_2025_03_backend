package stud.ntnu.backend.repository.news;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.news.NewsArticle;

import java.util.List;

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

    /**
     * Find all news articles ordered by published date (newest first) with pagination.
     *
     * @param pageable pagination information
     * @return a page of news articles
     */
    Page<NewsArticle> findAllByOrderByPublishedAtDesc(Pageable pageable);

    /**
     * Find all news articles for crisis events with the specified IDs with pagination.
     *
     * @param crisisEventIds the list of crisis event IDs
     * @param pageable pagination information
     * @return a page of news articles
     */
    @Query("SELECT n FROM NewsArticle n WHERE n.crisisEvent.id IN :crisisEventIds ORDER BY n.publishedAt DESC")
    Page<NewsArticle> findByCrisisEventIdInOrderByPublishedAtDesc(
            @Param("crisisEventIds") List<Integer> crisisEventIds,
            Pageable pageable);
}