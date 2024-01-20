package org.project.simproject.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.project.simproject.service.ArticleService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/article")
@Tag(name = "게시글 시스템", description = "게시글 관련 기능")
public class ArticleController {
    private final ArticleService articleService;
}
