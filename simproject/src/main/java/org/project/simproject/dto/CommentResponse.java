package org.project.simproject.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.simproject.domain.Comment;

@Getter
@NoArgsConstructor
public class CommentResponse {
    private String nickname;
    private String content;
    private int likesCount;

    public CommentResponse(Comment comment){
        this.nickname = comment.getNickname();
        this.content = comment.getContent();
        this.likesCount = comment.getLikesCount();
    }
}
