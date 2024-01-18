package org.project.simproject.repository;

import org.project.simproject.domain.Article;
import org.project.simproject.domain.Bookmark;
import org.project.simproject.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Optional<Bookmark> findByUserId(Long userId);
    List<Bookmark> findBookmarksByUserId(Long userId);
    Bookmark findBookmarkByArticleAndUser(Article article, User user);
    boolean existsBookmarkByArticleAndUser(Article article, User user);
}
