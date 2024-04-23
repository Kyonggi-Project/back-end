package org.project.simproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.OTTContents;
import org.project.simproject.domain.OTTReview;
import org.project.simproject.domain.User;
import org.project.simproject.dto.request.AddOTTReviewRequest;
import org.project.simproject.dto.request.ModifyOTTReviewRequest;
import org.project.simproject.dto.response.OTTReviewResponse;
import org.project.simproject.service.OTTReviewService;
import org.project.simproject.service.OTTService;
import org.project.simproject.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ottReview")
@Tag(name = "리뷰 시스템", description = "리뷰 관련 기능")
public class OTTReviewController {
    private final OTTReviewService ottReviewService;

    private final OTTService ottService;

    private final UserService userService;

    @Operation(summary = "리뷰 추가하기", description = "특정 OTT 컨텐츠에 대한 리뷰 데이터 추가")
    @PostMapping("/add/{ottId}")
    public ResponseEntity<OTTReview> save(@PathVariable String ottId, @RequestParam Long userId,
                                          @RequestBody AddOTTReviewRequest request){
        OTTContents ott = ottService.findById(ottId);
        User user = userService.findById(userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(ottReviewService.save(user, ott, request));
    }

    @Operation(summary = "리뷰 보기(OTT)", description = "특정 OTT 컨텐츠에 대한 모든 리뷰 데이터 보기")
    @GetMapping("/reviews/ott/{ottId}")
    public ResponseEntity<List<OTTReviewResponse>> getOTTReviewByOTTId(@PathVariable String ottId){
        return ResponseEntity.status(HttpStatus.OK).body(ottReviewService.findByOTTId(ottId));
    }

    @Operation(summary = "리뷰 보기(User)", description = "특정 유저에 대한 모든 리뷰 데이터 보기")
    @GetMapping("/reviews/user")
    public ResponseEntity<List<OTTReviewResponse>> getOTTReviewByUser(@RequestParam Long userId){
        User user = userService.findById(userId);

        return ResponseEntity.status(HttpStatus.OK).body(ottReviewService.findByUserId(user));
    }

    @Operation(summary = "리뷰 수정하기", description = "특정 OTT 컨텐츠에 대한 특정 리뷰 데이터 수정")
    @PutMapping("/modify/{ottReviewId}")
    public ResponseEntity<OTTReview> modify(@PathVariable Long ottReviewId, @RequestParam Long userId,
                                            @RequestBody ModifyOTTReviewRequest request){
        User user = userService.findById(userId);

        return ResponseEntity.status(HttpStatus.OK).body(ottReviewService.modify(ottReviewId, user, request));
    }

    @Operation(summary = "리뷰 삭제하기", description = "특정 OTT 컨텐츠에 대한 특정 리뷰 데이터 삭제")
    @DeleteMapping("/delete/{ottReviewId}")
    public ResponseEntity<Void> delete(@PathVariable Long ottReviewId, @RequestParam Long userId){
        User user = userService.findById(userId);

        ottReviewService.delete(ottReviewId, user);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}