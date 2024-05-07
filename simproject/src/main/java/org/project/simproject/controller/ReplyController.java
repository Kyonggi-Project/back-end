package org.project.simproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.OTTReview;
import org.project.simproject.domain.Reply;
import org.project.simproject.domain.User;
import org.project.simproject.dto.request.AddReplyRequest;
import org.project.simproject.dto.request.ModifyReplyRequest;
import org.project.simproject.dto.response.ReplyResponse;
import org.project.simproject.service.OTTReviewService;
import org.project.simproject.service.ReplyService;
import org.project.simproject.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reply")
@Tag(name = "Reply 시스템", description = "댓글 관련 기능")
public class ReplyController {
    private final UserService userService;

    private final OTTReviewService ottReviewService;

    private final ReplyService replyService;

    @Operation(summary = "댓글 추가하기", description = "특정 OTT 리뷰에 대한 댓긓 데이터 추가")
    @PostMapping("/add")
    public ResponseEntity<Reply> save(@RequestBody AddReplyRequest request, @RequestParam Long ottReviewId,
                                      @RequestParam Long userId){
        OTTReview ottReview = ottReviewService.findById(ottReviewId);
        User user = userService.findById(userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(replyService.save(request, ottReview, user));
    }

    @Operation(summary = "댓글 보기", description = "특정 OTT 리뷰에 대한 댓긓 모두 보기")
    @GetMapping("/view")
    public ResponseEntity<List<ReplyResponse>> findByOTTReviewId(@RequestParam Long ottReviewId){
        OTTReview ottReview = ottReviewService.findById(ottReviewId);

        return ResponseEntity.status(HttpStatus.OK).body(replyService.findByOTTReviewId(ottReview));
    }

    @Operation(summary = "댓글 수정", description = "특정 OTT 리뷰에 대한 튻정 댓글 수정")
    @PutMapping("/update")
    public ResponseEntity<Reply> modify(@RequestBody ModifyReplyRequest request, @RequestParam Long replyId,
                                        @RequestParam Long userId){
        User user = userService.findById(userId);

        return ResponseEntity.status(HttpStatus.OK).body(replyService.modify(request, replyId, user));
    }

    @Operation(summary = "댓글 삭제", description = "특정 OTT 리뷰에 대한 튻정 댓글 삭제")
    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestParam Long replyId, @RequestParam Long userId){
        User user = userService.findById(userId);

        replyService.delete(replyId, user);

        return ResponseEntity.ok("Reply Delete Successfully");
    }
}
