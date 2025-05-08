package stud.ntnu.backend.controller.news;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import stud.ntnu.backend.dto.news.NewsArticleDTO;
import stud.ntnu.backend.dto.news.NewsArticleResponseDTO;
import stud.ntnu.backend.dto.news.UpdateNewsArticleDTO;
import stud.ntnu.backend.model.news.NewsArticle;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.security.AdminChecker;
import stud.ntnu.backend.service.crisis.NewsService;
import stud.ntnu.backend.service.user.UserService;

import java.security.Principal;
import java.util.NoSuchElementException;

@RestController
@Validated
@RequestMapping("/api")
public class NewsController {

  private final NewsService newsService;
  private final UserService userService;

  @Autowired
  public NewsController(NewsService newsService, UserService userService) {
    this.newsService = newsService;
    this.userService = userService;
  }

  /**
   * Creates a new news article. Only users with ADMIN or SUPERADMIN roles can create news
   * articles.
   *
   * @param newsArticleDTO - The news article to create
   * @param principal      - The principal object containing the user's email
   * @return - The created news article or 403 Forbidden if unauthorized
   */
  @PostMapping("/admin/news")
  public ResponseEntity<?> createNewsArticle(@Validated @RequestBody NewsArticleDTO newsArticleDTO,
      Principal principal) {
    try {
      // Check if the current user is an admin using AdminChecker
      if (!AdminChecker.isCurrentUserAdmin(principal, userService)) {
        return ResponseEntity.status(403).body("Only administrators can create news articles");
      }

      // Get user from email
      User user = userService.getUserByEmail(principal.getName())
          .orElseThrow(() -> new IllegalStateException("User not found"));

      // Create the news article
      NewsArticle createdArticle = newsService.createNewsArticle(newsArticleDTO, user.getId());
      return ResponseEntity.ok(createdArticle);
    } catch (IllegalStateException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Get a news article by its ID.
   *
   * @param newsArticleId the ID of the news article
   * @return the news article
   */
  @GetMapping("/public/news/article/{newsArticleId}")
  public ResponseEntity<?> getNewsArticleById(@PathVariable Long newsArticleId) {
    try {
      NewsArticle newsArticle = newsService.getNewsArticleById(newsArticleId);
      return ResponseEntity.ok(newsArticle);
    } catch (NoSuchElementException e) {
      return ResponseEntity.notFound().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Get paginated news articles for a specific crisis event.
   *
   * @param crisisEventId the crisis event ID
   * @param pageable      pagination information
   * @return ResponseEntity with a page of news articles if successful, or an error message if the
   * crisis event doesn't exist
   */
  @GetMapping("/public/news/crisis/{crisisEventId}")
  public ResponseEntity<?> getNewsArticlesByCrisisEvent(
      @PathVariable Integer crisisEventId,
      Pageable pageable, Principal principal) {

    try {
      Page<NewsArticleResponseDTO> newsArticles = newsService.getNewsArticlesByCrisisEvent(
          crisisEventId, pageable);
      return ResponseEntity.ok(newsArticles);
    } catch (NoSuchElementException e) {
      return ResponseEntity.notFound().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Get paginated news articles for crisis events that are within 100 km of the user's location.
   * This includes both the user's home address and the user's household address.
   *
   * @param principal the Principal object representing the current user
   * @param pageable  pagination information
   * @return ResponseEntity with a page of news articles if successful, or an error message if the
   * user is not found
   */
  @GetMapping("/user/news/digest")
  public ResponseEntity<?> getNewsDigest(Principal principal, Pageable pageable) {
    try {
      // Get the current user
      User user = userService.getUserByEmail(principal.getName())
          .orElseThrow(() -> new IllegalStateException("User not found"));

      // Get news articles for crisis events within 100 km of the user's location
      Page<NewsArticleResponseDTO> newsArticles = newsService.getNewsDigestForUser(user, 100.0,
          pageable);

      return ResponseEntity.ok(newsArticles);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Updates an existing news article. Only users with ADMIN or SUPERADMIN roles can update news
   * articles. Title, content, and status can be updated.
   *
   * @param newsArticleId - The ID of the news article to update
   * @param updateDto     - The update data containing new title and/or content
   * @param principal     - The principal object containing the user's email
   * @return - The updated news article or 403 Forbidden if unauthorized
   */
  @PatchMapping("/admin/news/{newsArticleId}")
  public ResponseEntity<?> updateNewsArticle(
      @PathVariable Long newsArticleId,
      @Validated @RequestBody UpdateNewsArticleDTO updateDto,
      Principal principal) {
    try {
      // Check if the current user is an admin using AdminChecker
      if (!AdminChecker.isCurrentUserAdmin(principal, userService)) {
        return ResponseEntity.status(403).body("Only administrators can update news articles");
      }

      // Update the news article
      NewsArticle updatedArticle = newsService.updateNewsArticle(newsArticleId, updateDto);
      return ResponseEntity.ok(updatedArticle);
    } catch (NoSuchElementException e) {
      return ResponseEntity.notFound().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Get paginated draft news articles.
   *
   * @param pageable pagination information
   * @return ResponseEntity with a page of draft news articles
   */
  @GetMapping("/public/news/drafts")
  public ResponseEntity<?> getDraftNewsArticles(Pageable pageable) {
    Page<NewsArticleResponseDTO> draftNewsArticles = newsService.getDraftNewsArticles(pageable);
    return ResponseEntity.ok(draftNewsArticles);
  }

  /**
   * Get the newest news articles, ordered by published date (newest first).
   * This endpoint is publicly accessible and only returns published articles.
   *
   * @param pageable pagination information
   * @return ResponseEntity with a page of news articles
   */
  @GetMapping("/public/news/latest")
  public ResponseEntity<?> getAllNews(Pageable pageable) {
    try {
      Page<NewsArticleResponseDTO> newsArticles = newsService.getNewestNewsArticles(pageable);
      return ResponseEntity.ok(newsArticles);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}
