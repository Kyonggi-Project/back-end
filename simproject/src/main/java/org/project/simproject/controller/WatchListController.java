package org.project.simproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.OTTContents;
import org.project.simproject.domain.User;
import org.project.simproject.service.OTTService;
import org.project.simproject.service.UserService;
import org.project.simproject.service.WatchListService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/watchList")
@Tag(name = "WatchList 시스템", description = "WatchList 관련 기능")
public class WatchListController {
    private final UserService userService;

    private final OTTService ottService;

    private final WatchListService watchListService;

    @Operation(summary = "WatchList toggle system", description = "WatchList 추가/삭제 토글 기능 실행")
    @PostMapping("/toggle")
    public ResponseEntity<String> toggleWatchList(@RequestParam String ottContentsId, @RequestParam Long userId){
        User user = userService.findById(userId);

        try {
            OTTContents ottContents = ottService.findById(ottContentsId);

            watchListService.toggle(ottContents, user.getEmail());
            return ResponseEntity.ok("WatchList toggle successfully.");
        } catch (Exception e){
            return ResponseEntity.internalServerError()
                    .body("WatchList toggle failed.");
        }
    }

    @Operation(summary = "WatchList delete system", description = "WatchList 삭제 기능 실행(WatchList 페이지에서 직접 삭제)")
    @PostMapping("/delete")
    public ResponseEntity<String> deleteWatchList(@RequestParam String ottContentsId, @RequestParam Long userId){
        User user = userService.findById(userId);

        try {
            OTTContents ottContents = ottService.findById(ottContentsId);

            watchListService.deleteBookmark(ottContents, user.getEmail());
            return ResponseEntity.ok("WatchList delete successfully.");
        } catch (Exception e){
            return ResponseEntity.internalServerError()
                    .body("WatchList delete failed.");
        }
    }
}