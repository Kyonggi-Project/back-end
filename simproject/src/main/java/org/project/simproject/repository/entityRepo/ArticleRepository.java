package org.project.simproject.repository.entityRepo;

import org.project.simproject.domain.Article;
import org.project.simproject.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByAuthor(User user);
    Page<Article> findByContentContainsOrTitleContains(String content, String title, Pageable pageable);
}
