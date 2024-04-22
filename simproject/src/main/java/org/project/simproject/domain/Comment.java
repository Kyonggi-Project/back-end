package org.project.simproject.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.project.simproject.dto.request.ModifyCommentRequest;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "article_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Article articleId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User userId;

    private String content;

    private int likesCount;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void addLike(){
        this.likesCount++;
    }

    public void deleteLike(){
        this.likesCount--;
    }

    public void modify(ModifyCommentRequest request){
        this.content = request.getContent();
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    public Comment(Article articleId, User userId, String content){
        this.articleId = articleId;
        this.userId = userId;
        this.content = content;
        this.likesCount = 0;
        this.updatedAt = LocalDateTime.now();
    }
}
