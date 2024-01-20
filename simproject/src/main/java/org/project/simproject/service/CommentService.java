package org.project.simproject.service;

import jakarta.transaction.Transactional;
import org.project.simproject.domain.Article;
import org.project.simproject.domain.Comment;
import org.project.simproject.domain.User;
import org.project.simproject.dto.AddCommentRequest;
import org.project.simproject.dto.ModifyCommentRequest;
import org.project.simproject.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    public Comment save(AddCommentRequest request, Article articleId, User userId){
        return commentRepository.save(request.toEntity(articleId, userId));
    }

    public List<Comment> findByArticleId(Long articleId){
        List<Comment> list = commentRepository.findByArticleId(articleId);

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
