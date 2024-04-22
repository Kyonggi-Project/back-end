package org.project.simproject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.Article;
import org.project.simproject.domain.ArticleLike;
import org.project.simproject.domain.User;
import org.project.simproject.dto.response.ArticleResponse;
import org.project.simproject.repository.entityRepo.ArticleLikeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.project.simproject.util.ConvertPage.convertListToPage;

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

    public Page<ArticleResponse> findLikedArticlesByUser(Long userId, Pageable pageable) {
        List<Article> list = userService.findById(userId).getArticleLikes()
                .stream()
                .map(ArticleLike::getArticle)
                .toList();

        return convertListToPage(list, pageable).map(ArticleResponse::new);
    }

    public boolean isLiked(Article article, User user) {
        return articleLikeRepository.existsArticleLikeByArticleAndUser(article, user);
    }

}
