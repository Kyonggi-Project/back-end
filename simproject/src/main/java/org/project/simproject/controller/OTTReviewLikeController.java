package org.project.simproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.User;
import org.project.simproject.service.OTTReviewLikeService;
import org.project.simproject.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ottReview-like")
@Tag(name = "리뷰 좋아요 시스템", description = "리뷰 좋아요 관련 기능")
public class OTTReviewLikeController {
    private final OTTReviewLikeService ottReviewLikeService;

    private final UserService userService;

    @Operation(summary = "리뷰 좋아요 토글", description = "리뷰 좋아요 추가/취소 기능 실행")
    @PostMapping("/toggle/{ottReviewId}")
    public ResponseEntity<String> toggleOTTReviewLike(@PathVariable Long ottReviewId, @RequestParam Long userId){
        User user = userService.findById(userId);

        try{
            ottReviewLikeService.toggle(ottReviewId, user);
            return ResponseEntity.ok("OTTReviewLike toggled successfully.");
        }catch (Exception e){
            return ResponseEntity.internalServerError()
                    .body("OTTReviewLike toggled failed. ErrorMessage : " + e.getMessage());
        }
    }
}
