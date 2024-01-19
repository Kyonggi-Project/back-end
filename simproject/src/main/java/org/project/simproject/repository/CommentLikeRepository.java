package org.project.simproject.repository;

import org.project.simproject.domain.Comment;
import org.project.simproject.domain.CommentLike;
import org.project.simproject.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByUserId(Long userId);

    List<CommentLike> findCommentLikesByUserId(Long userId);

    CommentLike findCommentLikeByCommentAndUser(Comment comment, User user);

    boolean existsCommentLikeByCommentAndUser(Comment comment, User user);
}
