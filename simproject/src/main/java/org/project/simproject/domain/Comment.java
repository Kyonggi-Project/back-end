package org.project.simproject.domain;

import jakarta.persistence.*;
import lombok.*;

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
    @Column(name = "articleId")
    private Long articleId;
    @Column(name = "nickname")
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
}
