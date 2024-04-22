package org.project.simproject.repository.entityRepo;

import org.project.simproject.domain.Comment;
import org.project.simproject.domain.CommentLike;
import org.project.simproject.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    CommentLike findCommentLikeByCommentAndUser(Comment comment, User user);

    boolean existsCommentLikeByCommentAndUser(Comment comment, User user);
}
