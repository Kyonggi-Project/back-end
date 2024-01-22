package org.project.simproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.Article;
import org.project.simproject.domain.Comment;
import org.project.simproject.dto.AddCommentRequest;
import org.project.simproject.dto.CommentResponse;
import org.project.simproject.dto.ModifyCommentRequest;
import org.project.simproject.service.ArticleService;
import org.project.simproject.service.CommentService;
import org.project.simproject.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
@Tag(name = "댓글 시스템", description = "댓글 관련 기능")
public class CommentController {
    private final CommentService commentService;
    private final ArticleService articleService;
    private final UserService userService;

    @Operation(summary = "댓글 추가하기", description = "댓글 서비스에서 데이터베이스에 특정 게시글에 대한 댓글 데이터 추가")
    @PostMapping("/addComment/{articleId}")
    public ResponseEntity<Comment> addComment(@PathVariable Long articleId,
                                              @RequestParam Long userId,
                                              @RequestBody AddCommentRequest request){
        Comment comment = commentService.save(request, articleService.findToId(articleId), userService.findToId(userId));
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    @Operation(summary = "댓글 수정하기", description = "ModifyRequest 가져오기")
    @PutMapping("/updateComment/{id}")
    public ResponseEntity<Comment> modifyComment(@PathVariable Long id,
                                                 @RequestBody ModifyCommentRequest request){
        Comment comment = commentService.modify(request, id);
        return ResponseEntity.status(HttpStatus.OK).body(comment);
    }

    @Operation(summary = "댓글 삭제하기", description = "특정 댓글 데이터 DB에서 삭제하기")
    @DeleteMapping("/deleteComment/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id){
        commentService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "특정 게시글 댓글 모두 보기", description = "댓글 서비스에서 모든 댓글 불러오기")
    @GetMapping("/comments/{articleId}")
    public ResponseEntity<List<CommentResponse>> getAllComments(@PathVariable Long articleId){
        Article article = articleService.findToId(articleId);
        List<CommentResponse> list = commentService.findByArticleId(article)
                .stream()
                .map(CommentResponse::new)
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(list);

    }
}
