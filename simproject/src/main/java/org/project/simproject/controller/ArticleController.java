package org.project.simproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.project.simproject.dto.ArticleResponse;
import org.project.simproject.service.ArticleService;
import org.project.simproject.service.BookmarkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/article")
@Tag(name = "게시글 시스템", description = "게시글 관련 기능")
public class ArticleController {
    private final ArticleService articleService;
    private final BookmarkService bookmarkService;

    @Operation(summary = "북마크 게시글 모두 보기", description = "북마크 서비스에서 DTO 가져오기")
    @GetMapping("/bookmarked")
    public ResponseEntity<List<ArticleResponse>> getBookmarkedArticles(@RequestParam Long userId){
        List<ArticleResponse> list = bookmarkService.findBookmarkedArticlesByUser(userId);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }
}
