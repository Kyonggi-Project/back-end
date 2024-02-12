package org.project.simproject.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.simproject.domain.Article;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ArticleResponse {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime creatAt;
    private LocalDateTime updateAt;
    private String author;
    private int likesCount;

    public ArticleResponse(Article article){
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.creatAt = article.getCreatedAt();
        this.updateAt = article.getUpdatedAt();
        this.author = article.getAuthor().getNickname();
        this.likesCount = article.getLikesCount();
    }
}
