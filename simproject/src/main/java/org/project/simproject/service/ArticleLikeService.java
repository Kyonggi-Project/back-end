package org.project.simproject.service;

import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.Article;
import org.project.simproject.domain.ArticleLike;
import org.project.simproject.domain.User;
import org.project.simproject.repository.ArticleLikeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleLikeService {

    private final ArticleLikeRepository articleLikeRepository;

    private final ArticleService articleService;

    public void toggleArticleLike(Long articleId, Long userId) {
        Article article = articleService.findToId(articleId);
//        User user = userService.findToId(userId);           // 추후 UserService 구현 후 추가
        User user = User.builder()
                .email("test")
                .password("test")
                .nickname("test")
                .build();

        if (isArticleLike(article, user)) {
            ArticleLike deleteLike = articleLikeRepository.findArticleLikeByArticleAndUser(article, user);

            article.likeDelete();
            articleLikeRepository.delete(deleteLike);
        } else {
            ArticleLike newArticleLike = ArticleLike.builder()
                    .article(article)
                    .user(user)
                    .build();

            article.likeAdd();
            articleLikeRepository.save(newArticleLike);
        }
    }

    public List<Article> findLikeArticlesByUser(Long userId) {
        return articleLikeRepository.findArticleLikesByUserId(userId)
                .stream()
                .map(ArticleLike::getArticle)
                .toList();
    }

    public boolean isArticleLike(Article article, User user) {
        return articleLikeRepository.existsArticleLikeByArticleAndUser(article, user);
    }

}
