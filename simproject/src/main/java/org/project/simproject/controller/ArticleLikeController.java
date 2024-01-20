package org.project.simproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.project.simproject.service.ArticleLikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/artilcle-like")
@Tag(name = "게시물 좋아요", description = "게시물 좋아요 기능")
public class ArticleLikeController {

    private final ArticleLikeService articleLikeService;

    @Operation(summary = "게시물 좋아요 토글", description = "좋아요 추가/삭제 여부는 Service 레이어에서 실행")
    @PostMapping("/toggle/{articleId}")
    public ResponseEntity<String> toggleBookmark(
            @RequestParam Long userId,
            @PathVariable Long articleId
    ) {
        try {
            articleLikeService.toggleArticleLike(userId, articleId);
            return ResponseEntity.ok("ArticleLike toggled successfully.");
        } catch (Exception exception) {
            return ResponseEntity.internalServerError()
                    .body("ArticleLike toggled failed. ErrorMessage : " + exception.getMessage());
        }
    }

}
