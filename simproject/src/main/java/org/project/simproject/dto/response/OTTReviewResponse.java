package org.project.simproject.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.simproject.domain.OTTContents;
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

    private int repliesCount;

    private double score;

    private String contentsTitle;

    private String backgroundImg;

    private boolean isLiked;

    public OTTReviewResponse(OTTReview ottReview){
        this.id = ottReview.getId();
        this.content = ottReview.getContent();
        this.author = ottReview.getUserId().getNickname();
        this.creatAt = ottReview.getCreatedAt();
        this.updateAt = ottReview.getUpdatedAt();
        this.likesCount = ottReview.getLikesCount();
        this.repliesCount = ottReview.getReplies().size();
        this.score = ottReview.getScore();
        this.contentsTitle = ottReview.getContentsTitle();
    }

    public OTTReviewResponse(OTTReview ottReview, OTTContents ottContents, boolean isLiked){
        this.id = ottReview.getId();
        this.content = ottReview.getContent();
        this.author = ottReview.getUserId().getNickname();
        this.creatAt = ottReview.getCreatedAt();
        this.updateAt = ottReview.getUpdatedAt();
        this.likesCount = ottReview.getLikesCount();
        this.repliesCount = ottReview.getReplies().size();
        this.score = ottReview.getScore();
        this.contentsTitle = ottContents.getTitle();
        this.backgroundImg = ottContents.getBackgroundImg();
        this.isLiked = isLiked;
    }
}
