package org.project.simproject.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ott_review_like")
public class OTTReviewLike {
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

    @Builder
    public OTTReviewLike(OTTReview ottReviewId, User userId){
        this.ottReviewId = ottReviewId;
        this.userId = userId;
    }
}
