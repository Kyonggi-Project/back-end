package org.project.simproject.service;

import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.Comment;
import org.project.simproject.domain.CommentLike;
import org.project.simproject.domain.User;
import org.project.simproject.repository.CommentLikeRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;

    private final UserService userService;
    private final CommentService commentService;

    public void toggle(Long commentId, Long userId) {
        Comment comment = commentService.findById(commentId);
        User user = userService.findById(userId);

        if (isLiked(comment, user)) {
            CommentLike deleteLike = commentLikeRepository.findCommentLikeByCommentAndUser(comment, user);

            comment.deleteLike();
            commentLikeRepository.delete(deleteLike);
        } else {
            CommentLike newCommentLike = CommentLike.builder()
                    .comment(comment)
                    .user(user)
                    .build();

            comment.addLike();
            commentLikeRepository.save(newCommentLike);
        }
    }

    public boolean isLiked(Comment comment, User user) {
        return commentLikeRepository.existsCommentLikeByCommentAndUser(comment, user);
    }

}
