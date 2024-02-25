package org.project.simproject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.Article;
import org.project.simproject.domain.ArticleTagRelation;
import org.project.simproject.domain.Tag;
import org.project.simproject.dto.response.ArticleResponse;
import org.project.simproject.repository.ArticleTagRelationRepository;
import org.project.simproject.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {       // DTO 이용하도록 추후에 수정

    private final TagRepository tagRepository;
    private final ArticleTagRelationRepository articleTagRelationRepository;

    private final ArticleService articleService;

    @Transactional
    public void save(Long articleId, String name) {

        // 같은 내용의 Tag의 존재 여부 확인. 존재하지 않을 시, Tag를 만들고 DB에 저장
        Tag tag = findByNameOrSave(name);

        Article article = articleService.findById(articleId);           // Article 검색 방법 다시 생각해보기

        ArticleTagRelation relation = articleTagRelationRepository.save(
                ArticleTagRelation.builder()
                        .article(article)
                        .tag(tag)
                        .build()
        );

        // Article과 Tag 관계 저장
        article.addTag(relation);
        tag.addArticle(relation);
    }

    public Tag findByNameOrSave(String name) {
        return tagRepository.findByName(name)
                .orElse(
                        tagRepository.save(
                                Tag.builder()
                                        .name(name)
                                        .build()
                        )
                );
    }

    public List<Tag> findTagsByArticle(Article article) {
        return articleTagRelationRepository.findAllByArticle(article).stream()
                .map(ArticleTagRelation::getTag)
                .toList();
    }

    public List<ArticleResponse> findArticlesByTag(Tag tag) {
        return articleTagRelationRepository.findAllByTag(tag).stream()
                .map(ArticleTagRelation::getArticle)
                .map(ArticleResponse::new)
                .toList();
    }

    public Tag findByName(String name) {
        return tagRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Tag Not Found"));
    }

    /*
    게시글 삭제 시 해당 게시글과 태그 간의 관계 테이블 삭제
    (태그 자체를 삭제하는 경우는 고려하지 않음)
     */
    @Transactional
    public void deleteRelations(Article article) {
        List<ArticleTagRelation> relations = articleTagRelationRepository.findArticleTagRelationsByArticle(article);

        if (relations.isEmpty()) return;

        articleTagRelationRepository.deleteAll(relations);

        for (ArticleTagRelation relation : relations) {
            relation.getTag().deleteArticle(relation);
        }
    }
}
