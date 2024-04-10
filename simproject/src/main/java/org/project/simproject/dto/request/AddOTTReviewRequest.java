package org.project.simproject.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.simproject.domain.OTTReview;
import org.project.simproject.domain.User;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AddOTTReviewRequest {
    private String content;

    private double score;

    public OTTReview toEntity(User userId, String ottId){
        return OTTReview.builder()
                .content(content)
                .score(score)
                .userId(userId)
                .ottId(ottId)
                .build();
    }
}
