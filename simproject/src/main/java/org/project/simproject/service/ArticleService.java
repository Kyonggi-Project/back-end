package org.project.simproject.service;

import lombok.Lombok;
import org.project.simproject.domain.Article;
import org.project.simproject.dto.AddArticleRequest;
import org.project.simproject.dto.ModifyArticleRequest;
import org.project.simproject.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleService {
    @Autowired
    ArticleRepository articleRepository;

    public Article save(AddArticleRequest request, String author){
        return articleRepository.save(request.toEntity(author));
    }

    public List<Article> findAll(){
        return articleRepository.findAll();
    }

    public Article findToId(Long id){
        return articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article Not Found"));
    }

    public Article modify(Long id, ModifyArticleRequest request){
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article Not Found"));
        article.modify(request);
        return article;
    }

    public void delete(Long id){
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article Not Found"));
        articleRepository.delete(article);
    }

    public List<Article> findToAuthor(String author){
        return articleRepository.findByAuthor(author);
    }
}
