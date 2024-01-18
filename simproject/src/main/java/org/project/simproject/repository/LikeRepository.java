package org.project.simproject.repository;

import org.project.simproject.domain.Article;
import org.project.simproject.domain.Like;
import org.project.simproject.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserId(Long userId);

    List<Like> findLikesByUserId(Long userId);

    Like findLikeByArticleAndUser(Article article, User user);

    boolean existsLikeByArticleAndUser(Article article, User user);
}
