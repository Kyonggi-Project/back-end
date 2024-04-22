package org.project.simproject.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tags")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "tag")
    private List<ArticleTagRelation> articleTagRelations = new ArrayList<>();

    public void addArticle(ArticleTagRelation relation) {
        articleTagRelations.add(relation);
    }

    public void deleteArticle(ArticleTagRelation relation) {
        articleTagRelations.remove(relation);
    }

    @Builder
    Tag(String name) {
        this.name = name;
    }
}
