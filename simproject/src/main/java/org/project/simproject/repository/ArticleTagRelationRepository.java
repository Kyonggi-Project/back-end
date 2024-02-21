package org.project.simproject.repository;

import org.project.simproject.domain.Article;
import org.project.simproject.domain.ArticleTagRelation;
import org.project.simproject.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleTagRelationRepository extends JpaRepository<ArticleTagRelation, Long> {
    List<ArticleTagRelation> findAllByTag(Tag tag);

    List<ArticleTagRelation> findArticleTagRelationsByArticle(Article article);

    List<ArticleTagRelation> findAllByArticle(Article article);
}
