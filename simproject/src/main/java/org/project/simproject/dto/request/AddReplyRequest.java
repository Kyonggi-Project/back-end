package org.project.simproject.dto.request;

import lombok.*;
import org.project.simproject.domain.OTTReview;
import org.project.simproject.domain.Reply;
import org.project.simproject.domain.User;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AddReplyRequest {
    private String content;

    public Reply toEntity(OTTReview ottReview, User user){
        return Reply.builder()
                .ottReviewId(ottReview)
                .userId(user)
                .content(content)
                .build();
    }
}
