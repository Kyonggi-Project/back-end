package org.project.simproject.service;

import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.Article;
import org.project.simproject.domain.Bookmark;
import org.project.simproject.dto.AddBookmarkRequest;
import org.project.simproject.repository.ArticleRepository;
import org.project.simproject.repository.BookmarkRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final ArticleRepository articleRepository;

    public Bookmark save(AddBookmarkRequest request, Long userId) {
        return bookmarkRepository.save(request.toEntity(userId));
    }

    public List<Article> findBookmarkedArticlesByUser(Long userId) {
        List<Long> bookmarkedArticlesIdList = bookmarkRepository.findBookmarksByUserId(userId).stream()
                .map(Bookmark::getArticleId).toList();

        List<Article> bookmarkedArticlesList = new ArrayList<>();

        for (Long id : bookmarkedArticlesIdList) {
            Article bookmarkedArticle = articleRepository.findById(id)
                    .orElseThrow();
            bookmarkedArticlesList.add(bookmarkedArticle);
        }

        return bookmarkedArticlesList;
    }

    public void delete(Long articleId, Long userId) {
        Bookmark deleteRequestBookmark = bookmarkRepository.findById(articleId)
                .orElseThrow(IllegalArgumentException::new);
        bookmarkRepository.delete(deleteRequestBookmark);
    }

    public boolean isArticleBookmarked(Long articleId, Long userId) {
        return bookmarkRepository.existsByArticleIdAndUserId(articleId, userId);
    }

}
