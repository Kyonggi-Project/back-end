package org.project.simproject.domain;

import jakarta.persistence.*;
import lombok.*;
import org.project.simproject.dto.ModifyArticleRequest;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "articles")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String author;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private int likesCount;

    @Builder
    public Article(String title, String content, String author){
        this.title = title;
        this.content = content;
        this.author = author;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.likesCount = 0;
    }

    public void modify(ModifyArticleRequest request){
        this.title = request.getTitle();
        this.content = request.getContent();
        this.updatedAt = LocalDateTime.now();
    }

}
