package org.project.simproject.service;

import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.Article;
import org.project.simproject.domain.Bookmark;
import org.project.simproject.domain.User;
import org.project.simproject.dto.ArticleResponse;
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
        User user = User.builder()                             // Test용 임시 User 생성
                .email("test")
                .password("test")
                .nickname("test")
                .build();

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
        return bookmarkRepository.findBookmarksByUserId(userId)
                .stream()
                .map(Bookmark::getArticle)
                .map(ArticleResponse::new)
                .toList();
    }

    public boolean isBookmarked(Article article, User user) {
        return bookmarkRepository.existsBookmarkByArticleAndUser(article, user);
    }

}
