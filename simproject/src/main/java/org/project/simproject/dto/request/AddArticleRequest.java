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
    private List<String> tagNames;  // 게시글 생성 시, Tag 객체 자체가 아닌 이름만을 입력받아 추가

    public Article toEntity(User author){
        return Article.builder()
                .title(title)
                .content(content)
                .author(author)
                .build();
    }
}
