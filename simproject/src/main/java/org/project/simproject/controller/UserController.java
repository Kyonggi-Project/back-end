package org.project.simproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.User;
import org.project.simproject.dto.AddUserRequest;
import org.project.simproject.dto.ModifyRequest;
import org.project.simproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
@Tag(name = "유저 시스템", description = "유저 관련 기능")
public class UserController {

    @Autowired
    private final UserService userService;

    //유저 추가
    @Operation(summary = "유저 추가", description = "유저 서비스에서 유저를 추가")
    @PostMapping("/Signup")
    public ResponseEntity<User> Signup(@RequestBody AddUserRequest addUserRequest) {
        try {
            User createuser = userService.createUser(addUserRequest);
            createuser.setArticlesCount(0);
            return new ResponseEntity<>(createuser, HttpStatus.CREATED);
        }catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
    //특정 유저 정보 보기
    @Operation(summary = "특정 유저 정보", description = "유저 서비스에서 특정 유저의 정보를 조회")
    @GetMapping("/profile/{email}")
    public ResponseEntity<User> getProfile(@PathVariable String email)
    {
        User showUser = userService.showUser(email);
        return new ResponseEntity<>(showUser, HttpStatus.OK);
    }
    //현재 유저 수정
    @Operation(summary = "유저 정보 수정", description = "유저 서비스에서 현재 유저의 정보를 수정")
    @PostMapping("/modify")
    public ResponseEntity<User> userModify(@RequestParam String email, @RequestBody ModifyRequest modifyRequest) {
        User modifyuser = userService.updateUser(email,modifyRequest);
        return new ResponseEntity<>(modifyuser,HttpStatus.OK);
    }
    //유저 삭제
    @Operation(summary = "유저 삭제", description = "유저 서비스에서 해당 유저를 삭제")
    @DeleteMapping("/delete/{email}")
    public ResponseEntity<String> delete(@RequestParam String email) {
        try {
            userService.delete(email);
            return ResponseEntity.ok("The user was deleted.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("You can't delete a user." + e.getMessage());
        }
    }
}
