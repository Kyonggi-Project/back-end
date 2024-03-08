package org.project.simproject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.Article;
import org.project.simproject.domain.ArticleLike;
import org.project.simproject.domain.User;
import org.project.simproject.dto.response.ArticleResponse;
import org.project.simproject.repository.ArticleLikeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleLikeService {

    private final ArticleLikeRepository articleLikeRepository;

    private final UserService userService;
    private final ArticleService articleService;

    @Transactional
    public void toggle(Long articleId, Long userId) {
        Article article = articleService.findById(articleId);
        User user = userService.findById(userId);

        if (isLiked(article, user)) {
            ArticleLike deleteLike = articleLikeRepository.findArticleLikeByArticleAndUser(article, user);

            article.deleteLike();
            articleLikeRepository.delete(deleteLike);
        } else {
            ArticleLike newArticleLike = ArticleLike.builder()
                    .article(article)
                    .user(user)
                    .build();

            article.addLike();
            articleLikeRepository.save(newArticleLike);
        }
    }

    public List<ArticleResponse> findLikedArticlesByUser(Long userId) {
        return userService.findById(userId).getArticleLikes()
                .stream()
                .map(ArticleLike::getArticle)
                .map(ArticleResponse::new)
                .toList();
    }

    public boolean isLiked(Article article, User user) {
        return articleLikeRepository.existsArticleLikeByArticleAndUser(article, user);
    }

}
