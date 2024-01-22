package org.project.simproject.repository;

import org.project.simproject.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    Optional<Article> findByAuthor(Long id);
    List<Article> findByContentContains(String content);
}
