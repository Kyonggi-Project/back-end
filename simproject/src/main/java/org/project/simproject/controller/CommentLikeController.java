package org.project.simproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.project.simproject.service.CommentLikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment-like")
@Tag(name = "댓글 좋아요", description = "댓글 좋아요 기능")
public class CommentLikeController {

    private final CommentLikeService commentLikeService;

    @Operation(summary = "댓글 좋아요 토글", description = "좋아요 추가/삭제 여부는 Service 레이어에서 실행")
    @PostMapping("/toggle")
    public ResponseEntity<String> toggleBookmark(
            @RequestParam Long userId,
            @RequestParam Long articleId
    ) {
        try {
            commentLikeService.toggleCommentLike(userId, articleId);
            return ResponseEntity.ok("CommentLike toggled successfully.");
        } catch (Exception exception) {
            return ResponseEntity.internalServerError()
                    .body("CommentLike toggled failed. ErrorMessage : " + exception.getMessage());
        }
    }

}
