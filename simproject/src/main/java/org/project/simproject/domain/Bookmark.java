package org.project.simproject.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "bookmarks")
public class Bookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    @Column(name = "articleId")
    private Long articleId;
    @Column(name = "userId")
    private Long userId;

    @Builder
    public Bookmark(Long articleId, Long userId){
        this.articleId = articleId;
        this.userId = userId;
    }
}
