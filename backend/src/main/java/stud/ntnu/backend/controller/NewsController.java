package stud.ntnu.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import stud.ntnu.backend.dto.news.NewsArticleDTO;
import stud.ntnu.backend.model.news.NewsArticle;
import stud.ntnu.backend.service.NewsService;

import java.security.Principal;


@RestController
@RequestMapping("/api/news")
@Validated
public class NewsController {

  private final NewsService newsService;

  @Autowired
  public NewsController(NewsService newsService) {
    this.newsService = newsService;
  }

  /**
   * TODO: untested
   * @param newsArticleDTO - The news article to create
   * @param principal - The principal object containing the user's ID
   * @return - The created news article
   */
  @PostMapping
  public ResponseEntity<?> createNewsArticle(@Validated @RequestBody NewsArticleDTO newsArticleDTO,
      Principal principal) {
    try {
      NewsArticle createdArticle = newsService.createNewsArticle(newsArticleDTO,
          Integer.parseInt(principal.getName()));
      return ResponseEntity.ok(createdArticle);
    } catch (IllegalStateException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}
