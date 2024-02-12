package org.project.simproject.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.simproject.domain.Comment;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CommentResponse {
    private Long id;
    private String nickname;
    private String content;
    private int likesCount;
    private LocalDateTime updatedAt;

    public CommentResponse(Comment comment){
        this.id = comment.getId();
        this.nickname = comment.getUserId().getNickname();
        this.content = comment.getContent();
        this.likesCount = comment.getLikesCount();
        this.updatedAt = comment.getUpdatedAt();
    }
}
