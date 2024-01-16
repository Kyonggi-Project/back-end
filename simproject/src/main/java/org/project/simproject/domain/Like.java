package org.project.simproject.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "likes")
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    @Column(name = "articleId")
    private Long articleId;
    @Column(name = "userId")
    private Long userId;

    @Builder
    public Like(Long articleId, Long userId){
        this.articleId = articleId;
        this.userId = userId;
    }
}
