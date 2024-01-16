package org.project.simproject.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.simproject.domain.Bookmark;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AddBookmarkRequest {
    private Long articleId;

    public Bookmark toEntity(Long userId){
        return Bookmark.builder()
                .articleId(articleId)
                .userId(userId)
                .build();
    }
}
