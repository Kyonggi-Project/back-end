package org.project.simproject.repository.entityRepo;

import org.project.simproject.domain.Article;
import org.project.simproject.domain.Bookmark;
import org.project.simproject.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Bookmark findBookmarkByArticleAndUser(Article article, User user);
    boolean existsBookmarkByArticleAndUser(Article article, User user);
}
