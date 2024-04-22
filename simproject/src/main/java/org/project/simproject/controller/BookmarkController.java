package org.project.simproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.project.simproject.service.BookmarkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookmark")
@Tag(name = "북마크", description = "북마크 기능")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @Operation(summary = "북마크 토글", description = "북마크 추가/삭제 여부는 Service 레이어에서 실행")
    @PostMapping("/toggle/{articleId}")
    public ResponseEntity<String> toggleBookmark(
            @RequestParam Long userId,
            @PathVariable Long articleId
    ) {
        try {
            bookmarkService.toggle(articleId, userId);
            return ResponseEntity.ok("Bookmark toggled successfully.");
        } catch (Exception exception) {
            return ResponseEntity.internalServerError()
                    .body("Bookmark toggled failed. ErrorMessage : " + exception.getMessage());
        }
    }

}
