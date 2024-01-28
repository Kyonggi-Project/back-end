package org.project.simproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.User;
import org.project.simproject.dto.AddUserRequest;
import org.project.simproject.dto.ModifyRequest;
import org.project.simproject.dto.UserResponse;
import org.project.simproject.service.FollowService;
import org.project.simproject.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
@Tag(name = "유저 시스템", description = "유저 관련 기능")
public class UserController {

    private final UserService userService;
    private final FollowService followService;

    //유저 추가
    @Operation(summary = "유저 추가", description = "유저 서비스에서 유저를 추가")
    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@RequestBody AddUserRequest addUserRequest) {
        try {
            User createuser = userService.createUser(addUserRequest);
            UserResponse userResponse = new UserResponse(createuser);
            return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
        }catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
    //특정 유저 정보 보기
    @Operation(summary = "특정 유저 정보", description = "유저 서비스에서 특정 유저의 정보를 조회")
    @GetMapping("/profile/{email}")
    public ResponseEntity<UserResponse> getProfile(@PathVariable String email)
    {
        User showUser = userService.showUser(email);
        return new ResponseEntity<>(new UserResponse(showUser), HttpStatus.OK);
    }
    //현재 유저 수정
    @Operation(summary = "유저 정보 수정", description = "유저 서비스에서 현재 유저의 정보를 수정")
    @PutMapping("/modify")
    public ResponseEntity<User> userModify(@RequestParam String email, @RequestBody ModifyRequest modifyRequest) {
        User modifyuser = userService.updateUser(email,modifyRequest);
        return new ResponseEntity<>(modifyuser,HttpStatus.OK);
    }

    @Operation(summary = "특정 유저의 팔로워 목록 보기", description = "FollowService에서 실행")
    @GetMapping("/follower/{email}")
    public ResponseEntity<List<UserResponse>> getFollower(@PathVariable String email){
        List<UserResponse> list = followService.findListOfFollower(email);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @Operation(summary = "특정 유저의 팔로잉 목록 보기", description = "FollowService에서 실행")
    @GetMapping("/following/{email}")
    public ResponseEntity<List<UserResponse>> getFollowing(@PathVariable String email){
        List<UserResponse> list = followService.findListOfFollowee(email);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    //유저 삭제
    @Operation(summary = "유저 삭제", description = "유저 서비스에서 해당 유저를 삭제")
    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestParam Long id) {
        try {
            userService.delete(id);
            return ResponseEntity.ok("The user was deleted.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("You can't delete a user.");
        }
    }
}
