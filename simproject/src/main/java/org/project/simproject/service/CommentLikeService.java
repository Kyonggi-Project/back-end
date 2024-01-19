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

    private final CommentService commentService;

    public void toggleCommentLike(Long commentId, Long userId) {
        Comment comment = commentService.findToId(commentId);
//        User user = userService.findToId(userId);           // 추후 UserService 구현 후 추가
        User user = User.builder()
                .email("test")
                .password("test")
                .nickname("test")
                .build();

        if (isCommentLike(comment, user)) {
            CommentLike deleteLike = commentLikeRepository.findCommentLikeByCommentAndUser(comment, user);

            comment.likeDelete();
            commentLikeRepository.delete(deleteLike);
        } else {
            CommentLike newCommentLike = CommentLike.builder()
                    .comment(comment)
                    .user(user)
                    .build();

            comment.likeAdd();
            commentLikeRepository.save(newCommentLike);
        }
    }

    public boolean isCommentLike(Comment comment, User user) {
        return commentLikeRepository.existsCommentLikeByCommentAndUser(comment, user);
    }

}
