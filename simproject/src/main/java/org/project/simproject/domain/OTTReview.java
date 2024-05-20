package org.project.simproject.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.project.simproject.dto.request.ModifyOTTReviewRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    private String contentsTitle;

    private String contentsPoster;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private int likesCount;

    @JsonIgnore
    @OneToMany(mappedBy = "ottReviewId")
    private List<Reply> replies = new ArrayList<>();

    private double score;

    public void addLike(){
        this.likesCount++;
    }

    public void deleteLike(){
        this.likesCount--;
    }

    public void modify(ModifyOTTReviewRequest request){
        this.content = request.getContent();
        this.score = request.getScore();
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    public OTTReview(String content, User userId, String ottId, String contentsTitle, String contentsPoster, double score){
        this.content = content;
        this.userId = userId;
        this.ottId = ottId;
        this.likesCount = 0;
        this.score = score;
        this.contentsTitle = contentsTitle;
        this.contentsPoster = contentsPoster;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
