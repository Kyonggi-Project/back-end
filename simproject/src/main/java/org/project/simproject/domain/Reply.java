package org.project.simproject.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.project.simproject.dto.request.ModifyReplyRequest;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "replies")
public class Reply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ott_review_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private OTTReview ottReviewId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User userId;

    private String content;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void modify(ModifyReplyRequest request){
        this.content = request.getContent();
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    public Reply(String content, User userId, OTTReview ottReviewId){
        this.content = content;
        this.userId = userId;
        this.ottReviewId = ottReviewId;
        this.updatedAt = LocalDateTime.now();
    }
}
