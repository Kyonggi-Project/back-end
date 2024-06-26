package org.project.simproject.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.project.simproject.dto.request.ModifyArticleRequest;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User author;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private int likesCount;

    @JsonIgnore
    @OneToMany(mappedBy = "articleId")
    private List<Comment> comments = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "article")
    private List<ArticleTagRelation> articleTagRelations = new ArrayList<>();

    public void addLike(){
        this.likesCount++;
    }

    public void deleteLike(){
        this.likesCount--;
    }

    public void addTag(ArticleTagRelation relation) {
        articleTagRelations.add(relation);
    }

    public void deleteTag(ArticleTagRelation relation) {
        articleTagRelations.remove(relation);
    }
    
    public void modify(ModifyArticleRequest request){
        this.title = request.getTitle();
        this.content = request.getContent();
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    public Article(String title, String content, User author){
        this.title = title;
        this.content = content;
        this.author = author;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.likesCount = 0;
    }
}
