package stud.ntnu.backend.repository.news;

import org.springframework.data.jpa.repository.JpaRepository;
import stud.ntnu.backend.model.news.NewsArticle;

public interface NewsArticleRepository extends JpaRepository<NewsArticle, Long> {
} 