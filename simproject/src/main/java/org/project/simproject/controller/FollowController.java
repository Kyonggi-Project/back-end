package org.project.simproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.project.simproject.service.FollowService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follow")
@Tag(name = "팔로우 시스템", description = "팔로우 관련 기능")
public class FollowController {
    private final FollowService followService;

    @Operation(summary = "팔로우 추가 및 삭제", description = "팔로우 추가/삭제는 Service단에서 실행")
    @PostMapping("/following/{nickname}")
    public ResponseEntity<Void> toggleFollow(Principal principal, @PathVariable String nickname){
        followService.toggle(principal.getName(), nickname);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
