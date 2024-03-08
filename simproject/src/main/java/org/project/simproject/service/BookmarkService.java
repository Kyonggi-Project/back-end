package org.project.simproject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.Article;
import org.project.simproject.domain.Bookmark;
import org.project.simproject.domain.User;
import org.project.simproject.dto.response.ArticleResponse;
import org.project.simproject.repository.BookmarkRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;

    private final UserService userService;
    private final ArticleService articleService;

    @Transactional
    public void toggle(Long articleId, Long userId) {
        Article article = articleService.findById(articleId);
        User user = userService.findById(userId);

        if (isBookmarked(article, user)) {
            Bookmark deleteBookmark = bookmarkRepository.findBookmarkByArticleAndUser(article, user);
            bookmarkRepository.delete(deleteBookmark);
        } else {
            Bookmark newBookmark = Bookmark.builder()
                    .article(article)
                    .user(user)
                    .build();

            bookmarkRepository.save(newBookmark);
        }
    }

    public List<ArticleResponse> findBookmarkedArticlesByUser(Long userId) {
        return userService.findById(userId).getBookmarks()
                .stream()
                .map(Bookmark::getArticle)
                .map(ArticleResponse::new)
                .toList();
    }

    public boolean isBookmarked(Article article, User user) {
        return bookmarkRepository.existsBookmarkByArticleAndUser(article, user);
    }

}
