package stud.ntnu.backend.service.crisis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.backend.dto.news.NewsArticleDTO;
import stud.ntnu.backend.dto.news.NewsArticleResponseDTO;
import stud.ntnu.backend.dto.news.UpdateNewsArticleDTO;
import stud.ntnu.backend.model.news.NewsArticle;
import stud.ntnu.backend.model.news.NewsArticle.ArticleStatus;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.model.map.CrisisEvent;
import stud.ntnu.backend.repository.news.NewsArticleRepository;
import stud.ntnu.backend.repository.user.UserRepository;
import stud.ntnu.backend.repository.map.CrisisEventRepository;
import stud.ntnu.backend.util.LocationUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
// IllegalStateException is in java.lang package, no need to import

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

  public NewsArticle createNewsArticle(NewsArticleDTO newsArticleDTO, Integer userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalStateException("User not found with id: " + userId));

    CrisisEvent crisisEvent = crisisEventRepository.findById(newsArticleDTO.getCrisisEventId())
        .orElseThrow(() -> new IllegalStateException(
            "Crisis event not found with id: " + newsArticleDTO.getCrisisEventId()));

    // TODO: implement logic to send notifications to users within some area / with some conditions
    NewsArticle newsArticle = new NewsArticle();
    newsArticle.setTitle(newsArticleDTO.getTitle());
    newsArticle.setContent(newsArticleDTO.getContent());
    newsArticle.setPublishedAt(LocalDateTime.now());
    newsArticle.setCreatedBy(user);
    newsArticle.setCrisisEvent(crisisEvent);
    newsArticle.setCreatedAt(LocalDateTime.now());
    newsArticle.setUpdatedAt(LocalDateTime.now());

    // Set status to 'published' by default if not provided
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
   * @param pageable      pagination information
   * @return a page of news article DTOs
   * @throws NoSuchElementException if the crisis event doesn't exist
   */
  @Transactional(readOnly = true)
  public Page<NewsArticleResponseDTO> getNewsArticlesByCrisisEvent(Integer crisisEventId,
      Pageable pageable) {
    // Check if the crisis event exists
    if (!crisisEventRepository.existsById(crisisEventId)) {
      throw new NoSuchElementException("Crisis event not found with id: " + crisisEventId);
    }

    // Get the news articles
    Page<NewsArticle> newsArticles = newsArticleRepository.findByCrisisEventId(crisisEventId,
        pageable);

    // Convert to DTOs
    return newsArticles.map(NewsArticleResponseDTO::fromEntity);
  }

  /**
   * Get paginated news articles for crisis events that are within a specified distance of the
   * user's location. This includes both the user's home address and the user's household address.
   *
   * @param user         the user
   * @param distanceInKm the distance in kilometers
   * @param pageable     pagination information
   * @return a page of news article DTOs
   */
  @Transactional(readOnly = true)
  public Page<NewsArticleResponseDTO> getNewsDigestForUser(User user, double distanceInKm,
      Pageable pageable) {
    // Get all crisis events
    List<CrisisEvent> allCrisisEvents = crisisEventRepository.findAll();

    // Filter crisis events that are within the specified distance of the user's location
    List<Integer> nearbyCrisisEventIds = allCrisisEvents.stream()
        .filter(crisisEvent -> LocationUtil.isCrisisEventNearUser(user, crisisEvent, distanceInKm))
        .map(CrisisEvent::getId)
        .collect(Collectors.toList());

    // If no nearby crisis events found, return an empty page
    if (nearbyCrisisEventIds.isEmpty()) {
      return Page.empty(pageable);
    }

    // Get published news articles for the nearby crisis events
    Page<NewsArticle> newsArticles = newsArticleRepository.findByCrisisEventIdInAndStatusOrderByPublishedAtDesc(
        nearbyCrisisEventIds, ArticleStatus.published, pageable);

    // Convert to DTOs
    return newsArticles.map(NewsArticleResponseDTO::fromEntity);
  }

  @Transactional
  public NewsArticle updateNewsArticle(Long newsArticleId, UpdateNewsArticleDTO updateDto) {
    NewsArticle newsArticle = newsArticleRepository.findById(newsArticleId)
        .orElseThrow(
            () -> new NoSuchElementException("News article not found with id: " + newsArticleId));

    // Update only the fields that are provided
    if (updateDto.getTitle() != null) {
      newsArticle.setTitle(updateDto.getTitle());
    }
    if (updateDto.getContent() != null) {
      newsArticle.setContent(updateDto.getContent());
    }
    if (updateDto.getStatus() != null) {
      newsArticle.setStatus(updateDto.getStatus());
    }

    // Update the timestamp
    newsArticle.setUpdatedAt(LocalDateTime.now());

    return newsArticleRepository.save(newsArticle);
  }

  @Transactional(readOnly = true)
  public NewsArticle getNewsArticleById(Long newsArticleId) {
    return newsArticleRepository.findById(newsArticleId)
        .orElseThrow(() -> new NoSuchElementException("News article not found with id: " + newsArticleId));
  }

  /**
   * Get paginated draft news articles.
   *
   * @param pageable pagination information
   * @return a page of draft news article DTOs
   */
  @Transactional(readOnly = true)
  public Page<NewsArticleResponseDTO> getDraftNewsArticles(Pageable pageable) {
    Page<NewsArticle> draftArticles = newsArticleRepository.findByStatus(ArticleStatus.draft, pageable);
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
    // Get published news articles ordered by published date (newest first)
    Page<NewsArticle> newsArticles = newsArticleRepository.findByStatusOrderByPublishedAtDesc(
        ArticleStatus.published, pageable);

    // Convert to DTOs
    return newsArticles.map(NewsArticleResponseDTO::fromEntity);
  }
}
