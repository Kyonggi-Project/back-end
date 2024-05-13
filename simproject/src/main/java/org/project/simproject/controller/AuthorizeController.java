package org.project.simproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.User;
import org.project.simproject.service.UserService;
import org.project.simproject.util.AuthorizeUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/authorize")
@Tag(name = "인증 시스템", description = "작성자 인증 관련 기능")
public class AuthorizeController {
    private final UserService userService;

    @Operation(summary = "사용자 인증", description = "특정 게시글의 작성자와 사용자의 일치 여부 확인")
    @GetMapping("/{author}")
    public ResponseEntity<String> authorize(@PathVariable String author, Principal principal){
        User user = userService.findByEmail(principal.getName());

        if(AuthorizeUtil.authorizeByAuthor(author, user.getNickname())){
            return ResponseEntity.status(HttpStatus.OK).body("Authorized Successfully");
        }
        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorized failed");
        }
    }
}
