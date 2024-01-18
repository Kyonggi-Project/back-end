package org.project.simproject.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.simproject.domain.Comment;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AddCommentRequest {
    private String content;

    public Comment toEntity(Long articleId, String nickname){
        return Comment.builder()
                .articleId(articleId)
                .content(content)
                .nickname(nickname)
                .build();
    }
}
