package org.project.simproject.repository;

import org.project.simproject.domain.Article;
import org.project.simproject.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    Optional<Article> findByAuthor(User user);
    List<Article> findByContentContainsOrTitleContains(String content, String title);
    List<Article> findByTitleContains(String title);
}
