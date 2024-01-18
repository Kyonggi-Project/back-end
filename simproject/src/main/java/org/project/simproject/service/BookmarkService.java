package org.project.simproject.service;

import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.Article;
import org.project.simproject.domain.Bookmark;
import org.project.simproject.domain.User;
import org.project.simproject.repository.BookmarkRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;

    private final ArticleService articleService;

    public void toggleBookmark(Long articleId, Long userId) {
        Article article = articleService.findToId(articleId);
//        User user = userService.findToId(userId);           // 추후 UserService 구현 후 추가
        User user = User.builder()
                .email("test")
                .password("test")
                .nickname("test")
                .build();

        if (isArticleBookmarked(article, user)) {
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

    public List<Article> findBookmarkedArticlesByUser(Long userId) {
        return bookmarkRepository.findBookmarksByUserId(userId)
                .stream()
                .map(Bookmark::getArticle)
                .toList();
    }

    public boolean isArticleBookmarked(Article article, User user) {
        return bookmarkRepository.existsBookmarkByArticleAndUser(article, user);
    }

}
