package org.project.simproject.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.simproject.domain.OTTReview;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class OTTReviewResponse {
    private Long id;

    private String content;

    private String author;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime creatAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime updateAt;

    private int likesCount;

    private double score;

    public OTTReviewResponse(OTTReview ottReview){
        this.id = ottReview.getId();
        this.content = ottReview.getContent();
        this.author = ottReview.getUserId().getNickname();
        this.creatAt = ottReview.getCreatedAt();
        this.updateAt = ottReview.getUpdatedAt();
        this.likesCount = ottReview.getLikesCount();
        this.score = ottReview.getScore();
    }
}
