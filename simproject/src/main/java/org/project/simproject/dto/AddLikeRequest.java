package org.project.simproject.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.simproject.domain.Like;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AddLikeRequest {
    private Long articleId;

    public Like toEntity(Long userId){
        return Like.builder()
                .articleId(articleId)
                .userId(userId)
                .build();
    }
}
