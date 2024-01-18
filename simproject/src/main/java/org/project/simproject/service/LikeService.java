package org.project.simproject.service;

import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.Article;
import org.project.simproject.domain.Like;
import org.project.simproject.domain.User;
import org.project.simproject.repository.LikeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;

    private final ArticleService articleService;

    public void toggleLike(Long articleId, Long userId) {
        Article article = articleService.findToId(articleId);
//        User user = userService.findToId(userId);           // 추후 UserService 구현 후 추가
        User user = User.builder()
                .email("test")
                .password("test")
                .nickname("test")
                .build();

        if (isArticleLike(article, user)) {
            Like deleteLike = likeRepository.findLikeByArticleAndUser(article, user);

            article.likeDelete();
            likeRepository.delete(deleteLike);
        } else {
            Like newLike = Like.builder()
                    .article(article)
                    .user(user)
                    .build();

            article.likeAdd();
            likeRepository.save(newLike);
        }
    }

    public List<Article> findLikeArticlesByUser(Long userId) {
        return likeRepository.findLikesByUserId(userId)
                .stream()
                .map(Like::getArticle)
                .toList();
    }

    public boolean isArticleLike(Article article, User user) {
        return likeRepository.existsLikeByArticleAndUser(article, user);
    }

}
