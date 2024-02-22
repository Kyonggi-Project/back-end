package org.project.simproject.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.simproject.domain.Article;
import org.project.simproject.domain.User;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AddArticleRequest {
    private String title;
    private String content;
    private List<String> tags;

    public Article toEntity(User author){
        return Article.builder()
                .title(title)
                .content(content)
                .author(author)
                .build();
    }
}
