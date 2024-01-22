package org.project.simproject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.Article;
import org.project.simproject.domain.User;
import org.project.simproject.dto.AddArticleRequest;
import org.project.simproject.dto.ModifyArticleRequest;
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

    public Article save(AddArticleRequest request, User user){
        user.articleAdd();
        return articleRepository.save(request.toEntity(user));
    }

    public List<Article> findAll(){
        return articleRepository.findAll();
    }

    public Article findToId(Long id){
        return articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article Not Found"));
    }

    public void delete(Long id, User user){
        user.articleDelete();

        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article Not Found"));
        articleRepository.delete(article);
    }

    @Transactional
    public Article modify(Long id, ModifyArticleRequest request){
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article Not Found"));
        article.modify(request);
        return article;
    }

    public List<Article> findToAuthor(String author){
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

    public List<Article> findToContent(String content){
        return articleRepository.findByContentContains(content);
    }
}
