package org.project.simproject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.Article;
import org.project.simproject.domain.User;
import org.project.simproject.dto.request.AddArticleRequest;
import org.project.simproject.dto.request.ModifyArticleRequest;
import org.project.simproject.repository.ArticleRepository;
import org.project.simproject.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final TagService tagService;

    public Article save(AddArticleRequest request, User user){
        user.addArticle();
        return articleRepository.save(request.toEntity(user));
    }

    public List<Article> findAll(){
        return articleRepository.findAll();
    }

    public Article findById(Long id){
        return articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article Not Found"));
    }

    public List<Article> findByAuthor(String author){
        List<Article> articleList = new ArrayList<>();
        List<User> list = userRepository.findByNicknameContains(author);
        for(User user : list){
            if(user.getArticlesCount() != 0){
                articleList.add(articleRepository.findByAuthor(user)
                        .orElseThrow(() -> new IllegalArgumentException("Article Not Found")));
            }
        }
        return articleList;
    }

    public List<Article> findByContent(String content){
        return articleRepository.findByContentContainsOrTitleContains(content, content);
    }

    @Transactional
    public Article modify(Long id, ModifyArticleRequest request){
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article Not Found"));
        article.modify(request);
        return article;
    }

    @Transactional
    public void delete(Long id, User user){
        user.deleteArticle();

        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article Not Found"));

        // Article에서 사용된 Tag와의 관계 테이블 삭제
        tagService.deleteRelations(article);
        articleRepository.delete(article);
    }
}
