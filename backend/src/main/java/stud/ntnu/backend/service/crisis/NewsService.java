package stud.ntnu.backend.service.crisis;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import stud.ntnu.backend.dto.news.NewsArticleDTO;
import stud.ntnu.backend.dto.news.NewsArticleResponseDTO;
import stud.ntnu.backend.dto.news.UpdateNewsArticleDTO;
import stud.ntnu.backend.model.map.CrisisEvent;
import stud.ntnu.backend.model.news.NewsArticle;
import stud.ntnu.backend.model.news.NewsArticle.ArticleStatus;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.repository.map.CrisisEventRepository;
import stud.ntnu.backend.repository.news.NewsArticleRepository;
import stud.ntnu.backend.repository.user.UserRepository;
import stud.ntnu.backend.util.LocationUtil;

@Service
@Transactional
public class NewsService {

  private final NewsArticleRepository newsArticleRepository;
  private final UserRepository userRepository;
  private final CrisisEventRepository crisisEventRepository;

  @Autowired
  public NewsService(NewsArticleRepository newsArticleRepository,
      UserRepository userRepository,
      CrisisEventRepository crisisEventRepository) {
    this.newsArticleRepository = newsArticleRepository;
    this.userRepository = userRepository;
    this.crisisEventRepository = crisisEventRepository;
  }

  /**
   * Creates a new news article for a specific crisis event.
   *
   * @param newsArticleDTO the DTO containing the news article data
   * @param userId the ID of the user creating the article
   * @return the created news article
   * @throws IllegalStateException if the user or crisis event is not found
   */
  public NewsArticle createNewsArticle(NewsArticleDTO newsArticleDTO, Integer userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalStateException("User not found with id: " + userId));

    CrisisEvent crisisEvent = crisisEventRepository.findById(newsArticleDTO.getCrisisEventId())
        .orElseThrow(() -> new IllegalStateException(
            "Crisis event not found with id: " + newsArticleDTO.getCrisisEventId()));

    NewsArticle newsArticle = new NewsArticle();
    newsArticle.setTitle(newsArticleDTO.getTitle());
    newsArticle.setContent(newsArticleDTO.getContent());
    newsArticle.setPublishedAt(LocalDateTime.now());
    newsArticle.setCreatedBy(user);
    newsArticle.setCrisisEvent(crisisEvent);
    newsArticle.setCreatedAt(LocalDateTime.now());
    newsArticle.setUpdatedAt(LocalDateTime.now());

    if (newsArticleDTO.getStatus() != null) {
      newsArticle.setStatus(newsArticleDTO.getStatus());
    } else {
      newsArticle.setStatus(ArticleStatus.published);
    }

    return newsArticleRepository.save(newsArticle);
  }

  /**
   * Get paginated news articles for a specific crisis event.
   *
   * @param crisisEventId the crisis event ID
   * @param pageable pagination information
   * @return a page of news article DTOs
   * @throws NoSuchElementException if the crisis event doesn't exist
   */
  @Transactional(readOnly = true)
  public Page<NewsArticleResponseDTO> getNewsArticlesByCrisisEvent(Integer crisisEventId,
      Pageable pageable) {
    if (!crisisEventRepository.existsById(crisisEventId)) {
      throw new NoSuchElementException("Crisis event not found with id: " + crisisEventId);
    }

    Page<NewsArticle> newsArticles = newsArticleRepository.findByCrisisEventIdOrderByPublishedAtDesc(crisisEventId,
        pageable);

    return newsArticles.map(NewsArticleResponseDTO::fromEntity);
  }

  /**
   * Get paginated news articles for crisis events that are within a specified distance of the
   * user's location. This includes both the user's home address and the user's household address.
   * Articles are returned in order of newest to oldest.
   *
   * @param user the user
   * @param distanceInKm the distance in kilometers
   * @param pageable pagination information
   * @return a page of news article DTOs
   */
  @Transactional(readOnly = true)
  public Page<NewsArticleResponseDTO> getNewsDigestForUser(User user, double distanceInKm,
      Pageable pageable) {
    List<CrisisEvent> allCrisisEvents = crisisEventRepository.findAll();

    List<Integer> nearbyCrisisEventIds = allCrisisEvents.stream()
        .filter(crisisEvent -> LocationUtil.isCrisisEventNearUser(user, crisisEvent, distanceInKm))
        .map(CrisisEvent::getId)
        .collect(Collectors.toList());

    if (nearbyCrisisEventIds.isEmpty()) {
      return Page.empty(pageable);
    }

    Page<NewsArticle> newsArticles = newsArticleRepository.findByCrisisEventIdInAndStatusOrderByPublishedAtDesc(
        nearbyCrisisEventIds, ArticleStatus.published, pageable);

    return newsArticles.map(NewsArticleResponseDTO::fromEntity);
  }

  /**
   * Updates an existing news article with new information.
   *
   * @param newsArticleId the ID of the news article to update
   * @param updateDto the DTO containing the updated information
   * @return the updated news article
   * @throws NoSuchElementException if the news article is not found
   */
  @Transactional
  public NewsArticle updateNewsArticle(Long newsArticleId, UpdateNewsArticleDTO updateDto) {
    NewsArticle newsArticle = newsArticleRepository.findById(newsArticleId)
        .orElseThrow(
            () -> new NoSuchElementException("News article not found with id: " + newsArticleId));

    if (updateDto.getTitle() != null) {
      newsArticle.setTitle(updateDto.getTitle());
    }
    if (updateDto.getContent() != null) {
      newsArticle.setContent(updateDto.getContent());
    }
    if (updateDto.getStatus() != null) {
      newsArticle.setStatus(updateDto.getStatus());
    }

    newsArticle.setUpdatedAt(LocalDateTime.now());

    return newsArticleRepository.save(newsArticle);
  }

  /**
   * Retrieves a news article by its ID.
   *
   * @param newsArticleId the ID of the news article to retrieve
   * @return the news article
   * @throws NoSuchElementException if the news article is not found
   */
  @Transactional(readOnly = true)
  public NewsArticle getNewsArticleById(Long newsArticleId) {
    return newsArticleRepository.findById(newsArticleId)
        .orElseThrow(() -> new NoSuchElementException("News article not found with id: " + newsArticleId));
  }

  /**
   * Get paginated draft news articles ordered by creation date (newest first).
   *
   * @param pageable pagination information
   * @return a page of draft news article DTOs
   */
  @Transactional(readOnly = true)
  public Page<NewsArticleResponseDTO> getDraftNewsArticles(Pageable pageable) {
    Page<NewsArticle> draftArticles = newsArticleRepository.findByStatusOrderByCreatedAtDesc(ArticleStatus.draft, pageable);
    return draftArticles.map(NewsArticleResponseDTO::fromEntity);
  }

  /**
   * Get the newest news articles, ordered by published date (newest first).
   * Only returns articles with status 'published'.
   *
   * @param pageable pagination information
   * @return a page of news article DTOs
   */
  @Transactional(readOnly = true)
  public Page<NewsArticleResponseDTO> getNewestNewsArticles(Pageable pageable) {
    Page<NewsArticle> newsArticles = newsArticleRepository.findByStatusOrderByPublishedAtDesc(
        ArticleStatus.published, pageable);

    return newsArticles.map(NewsArticleResponseDTO::fromEntity);
  }
}
