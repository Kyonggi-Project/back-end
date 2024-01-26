package org.project.simproject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.Article;
import org.project.simproject.domain.Comment;
import org.project.simproject.domain.User;
import org.project.simproject.dto.AddCommentRequest;
import org.project.simproject.dto.ModifyCommentRequest;
import org.project.simproject.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final ArticleService articleService;

    public Comment save(AddCommentRequest request, Article articleId, User userId){
        return commentRepository.save(request.toEntity(articleId, userId));
    }

    public List<Comment> findByArticleId(Article article){
        List<Comment> list = articleService.findToId(article.getId()).getComments();

        return list;
    }

    public void delete(Long id){
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment Not Found"));
        commentRepository.delete(comment);
    }

    @Transactional
    public Comment modify(ModifyCommentRequest request, Long id){
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment Not Found"));
        comment.modify(request);

        return comment;
    }

    public Comment findToId(Long id){
        return commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment Not Found"));
    }
}
