package org.project.simproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.project.simproject.dto.response.ArticleResponse;
import org.project.simproject.service.TagService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tag")
@Tag(name = "태그", description = "태그 기능")
public class TagController {
    private final TagService tagService;

    @Operation(summary = "태그를 이용해 게시글 찾기", description = "해당 태그를 사용하고 있는 게시글 모두 찾기")
    @GetMapping("/search/{tagName}")
    public List<ArticleResponse> findArticlesByTag(@PathVariable String tagName) {
        org.project.simproject.domain.Tag tag = tagService.findByName(tagName);
        return tagService.findArticlesByTag(tag);
    }
}
