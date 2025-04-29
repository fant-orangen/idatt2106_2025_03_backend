package stud.ntnu.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.backend.dto.news.NewsArticleDTO;
import stud.ntnu.backend.model.news.NewsArticle;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.model.map.CrisisEvent;
import stud.ntnu.backend.repository.news.NewsArticleRepository;
import stud.ntnu.backend.repository.user.UserRepository;
import stud.ntnu.backend.repository.map.CrisisEventRepository;

import java.time.LocalDateTime;
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

    NewsArticle newsArticle = new NewsArticle();
    newsArticle.setTitle(newsArticleDTO.getTitle());
    newsArticle.setContent(newsArticleDTO.getContent());
    newsArticle.setPublishedAt(LocalDateTime.now());
    newsArticle.setCreatedBy(user);
    newsArticle.setCrisisEvent(crisisEvent);
    newsArticle.setCreatedAt(LocalDateTime.now());
    newsArticle.setUpdatedAt(LocalDateTime.now());

    return newsArticleRepository.save(newsArticle);
  }
}
