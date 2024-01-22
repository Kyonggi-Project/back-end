package org.project.simproject.repository;

import org.project.simproject.domain.Article;
import org.project.simproject.domain.ArticleLike;
import org.project.simproject.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {
    List<ArticleLike> findByUserId(Long userId);

    ArticleLike findArticleLikeByArticleAndUser(Article article, User user);

    boolean existsArticleLikeByArticleAndUser(Article article, User user);
}
