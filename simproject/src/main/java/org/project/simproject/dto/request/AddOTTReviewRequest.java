package org.project.simproject.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.simproject.domain.OTTReview;
import org.project.simproject.domain.User;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AddOTTReviewRequest {
    private String content;

    private double score;

    private List<String> tags = new ArrayList<>();

    private List<String> inputTags = new ArrayList<>();

    public OTTReview toEntity(User userId, String ottId, String contentsTitle, String contentsPoster){
        return OTTReview.builder()
                .content(content)
                .score(score)
                .userId(userId)
                .ottId(ottId)
                .contentsTitle(contentsTitle)
                .contentsPoster(contentsPoster)
                .build();
    }
}
