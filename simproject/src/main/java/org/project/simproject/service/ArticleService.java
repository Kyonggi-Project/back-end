package org.project.simproject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.Article;
import org.project.simproject.domain.User;
import org.project.simproject.dto.request.AddArticleRequest;
import org.project.simproject.dto.request.ModifyArticleRequest;
import org.project.simproject.dto.response.ArticleResponse;
import org.project.simproject.repository.ArticleRepository;
import org.project.simproject.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.project.simproject.util.ConvertPage.convertListToPage;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final TagService tagService;

    @Transactional
    public Article save(AddArticleRequest request, User user){
        Article article = request.toEntity(user);

        if (!request.getTagNames().isEmpty()) {
            for (String tagName : request.getTagNames()) {
                tagService.save(article, tagName);
            }
        }
        user.addArticle();

        return articleRepository.save(article);
    }

    public Page<ArticleResponse> findAll(Pageable pageable){
        return articleRepository.findAll(pageable).map(ArticleResponse::new);
    }

    public Article findById(Long id){
        return articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article Not Found"));
    }

    public Page<ArticleResponse> findByAuthor(String author, Pageable pageable){
        List<Article> articleList = new ArrayList<>();
        List<User> list = userRepository.findByNicknameContains(author);
        for(User user : list){
            if(user.getArticlesCount() != 0){
                articleList.addAll(articleRepository.findByAuthor(user));
            }
        }
        Page<ArticleResponse> articlePage = convertListToPage(articleList, pageable).map(ArticleResponse::new);
        return articlePage;
    }

    public Page<ArticleResponse> findByContent(String content, Pageable pageable){
        return articleRepository.findByContentContainsOrTitleContains(content, content, pageable).map(ArticleResponse::new);
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
