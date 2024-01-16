package org.project.simproject.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "article")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "content", nullable = false)
    private String content;
    @Column(name = "author", nullable = false)
    private String author;
    @CreatedDate
    @Column(name = "createTime")
    private LocalDateTime createTime;
    @LastModifiedDate
    @Column(name = "updateTime")
    private LocalDateTime updateTime;
    @Column(name = "likesCount")
    private int likesCount;

    @Builder
    public Article(String title, String content, String author){
        this.title = title;
        this.content = content;
        this.author = author;
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
        this.likesCount = 0;
    }

}
