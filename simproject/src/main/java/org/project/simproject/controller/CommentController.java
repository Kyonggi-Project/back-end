package org.project.simproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.Comment;
import org.project.simproject.dto.AddCommentRequest;
import org.project.simproject.dto.CommentResponse;
import org.project.simproject.service.ArticleService;
import org.project.simproject.service.CommentService;
import org.project.simproject.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
@Tag(name = "댓글 시스템", description = "댓글 관련 기능")
public class CommentController {



}
