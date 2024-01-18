package org.project.simproject.domain;

import jakarta.persistence.*;
import lombok.*;
import org.project.simproject.dto.ModifyCommentRequest;

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

    @Column( nullable = false)
    private Long articleId;

    @Column(nullable = false)
    private String nickname;

    private String content;

    private int likesCount;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Comment(Long articleId, String nickname, String content){
        this.articleId = articleId;
        this.nickname = nickname;
        this.content = content;
        this.likesCount = 0;
        this.updatedAt = LocalDateTime.now();
    }

    public void modify(ModifyCommentRequest request){
        this.content = request.getContent();
        this.updatedAt = LocalDateTime.now();
    }
}
