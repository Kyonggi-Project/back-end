package org.project.simproject.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ott_reviews")
public class OTTReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User userId;

    private String ottId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private int likesCount;

    private double score;

    public void addLike(){
        this.likesCount++;
    }

    public void deleteLike(){
        this.likesCount--;
    }

    @Builder
    public OTTReview(String content, User userId, String ottId, double score){
        this.content = content;
        this.userId = userId;
        this.ottId = ottId;
        this.likesCount = 0;
        this.score = score;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
