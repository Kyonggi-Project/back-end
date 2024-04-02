package org.project.simproject.repository.entityRepo;

import org.project.simproject.domain.Article;
import org.project.simproject.domain.ArticleLike;
import org.project.simproject.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {
    ArticleLike findArticleLikeByArticleAndUser(Article article, User user);

    boolean existsArticleLikeByArticleAndUser(Article article, User user);
}
