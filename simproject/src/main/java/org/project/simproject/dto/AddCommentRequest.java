package org.project.simproject.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.simproject.domain.Article;
import org.project.simproject.domain.Comment;
import org.project.simproject.domain.User;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AddCommentRequest {
    private String content;

    public Comment toEntity(Article articleId, User userId){
        return Comment.builder()
                .articleId(articleId)
                .content(content)
                .userId(userId)
                .build();
    }
}
