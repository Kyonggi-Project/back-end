package org.project.simproject.domain;

import jakarta.persistence.*;
import lombok.*;
import org.project.simproject.dto.ModifyCommentRequest;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    @Column(name = "articleId", nullable = false)
    private Long articleId;
    @Column(name = "nickname", nullable = false)
    private String nickname;
    @Column(name = "content")
    private String content;
    @Column(name = "likesCount")
    private int likesCount;

    @Builder
    public Comment(Long articleId, String nickname, String content){
        this.articleId = articleId;
        this.nickname = nickname;
        this.content = content;
        this.likesCount = 0;
    }

    public void modify(ModifyCommentRequest request){
        this.content = request.getContent();
    }
}
