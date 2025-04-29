package stud.ntnu.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import stud.ntnu.backend.dto.news.NewsArticleDTO;
import stud.ntnu.backend.dto.news.NewsArticleResponseDTO;
import stud.ntnu.backend.model.news.NewsArticle;
import stud.ntnu.backend.security.AdminChecker;
import stud.ntnu.backend.service.NewsService;
import stud.ntnu.backend.service.UserService;

import java.security.Principal;
import java.util.NoSuchElementException;


@RestController
@RequestMapping("/api/news")
@Validated
public class NewsController {

  private final NewsService newsService;
  private final UserService userService;

  @Autowired
  public NewsController(NewsService newsService, UserService userService) {
    this.newsService = newsService;
    this.userService = userService;
  }

  /**
   * Creates a new news article.
   * Only users with ADMIN or SUPERADMIN roles can create news articles.
   *
   * @param newsArticleDTO - The news article to create
   * @param principal - The principal object containing the user's email
   * @return - The created news article or 403 Forbidden if unauthorized
   */
  @PostMapping
  public ResponseEntity<?> createNewsArticle(@Validated @RequestBody NewsArticleDTO newsArticleDTO,
      Principal principal) {
    try {
      // Check if the current user is an admin using AdminChecker
      if (!AdminChecker.isCurrentUserAdmin(principal, userService)) {
        return ResponseEntity.status(403).body("Only administrators can create news articles");
      }

      // Get user ID from principal
      Integer userId = Integer.parseInt(principal.getName());

      // Create the news article
      NewsArticle createdArticle = newsService.createNewsArticle(newsArticleDTO, userId);
      return ResponseEntity.ok(createdArticle);
    } catch (IllegalStateException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Get paginated news articles for a specific crisis event.
   *
   * @param crisisEventId the crisis event ID
   * @param pageable pagination information
   * @return ResponseEntity with a page of news articles if successful, or an error message if the crisis event doesn't exist
   */
  @GetMapping("/{crisisEventId}")
  public ResponseEntity<?> getNewsArticlesByCrisisEvent(
      @PathVariable Integer crisisEventId,
      Pageable pageable, Principal principal) {

    if (!AdminChecker.isCurrentUserAdmin(principal, userService)) {
      return ResponseEntity.status(403).body("Only administrators can access this resource");
    }
    try {
      Page<NewsArticleResponseDTO> newsArticles = newsService.getNewsArticlesByCrisisEvent(crisisEventId, pageable);
      return ResponseEntity.ok(newsArticles);
    } catch (NoSuchElementException e) {
      return ResponseEntity.notFound().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}
