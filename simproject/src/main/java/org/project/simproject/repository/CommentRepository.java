package org.project.simproject.repository;

import org.project.simproject.domain.Article;
import org.project.simproject.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByArticleId(Article article);
}
