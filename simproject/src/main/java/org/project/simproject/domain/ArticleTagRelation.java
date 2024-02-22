package org.project.simproject.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "article_tag")
public class ArticleTagRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "article_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Article article;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Tag tag;

    @Builder
    ArticleTagRelation(Article article, Tag tag) {
        this.article = article;
        this.tag = tag;
    }
}
