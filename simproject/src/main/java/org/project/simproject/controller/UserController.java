package org.project.simproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.User;
import org.project.simproject.domain.WatchList;
import org.project.simproject.dto.request.AddUserRequest;
import org.project.simproject.dto.request.ModifyRequest;
import org.project.simproject.dto.response.UserResponse;
import org.project.simproject.service.FollowService;
import org.project.simproject.service.UserService;
import org.project.simproject.service.WatchListService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(name = "유저 시스템", description = "유저 관련 기능")
public class UserController {

    private final UserService userService;
    private final FollowService followService;
    private final WatchListService watchListService;

    //유저 추가
    @Operation(summary = "유저 추가", description = "유저 서비스에서 유저를 추가")
    @PostMapping("/signup")
    public ResponseEntity<UserResponse> addUser(@RequestBody AddUserRequest addUserRequest) {
        try {
            User createuser = userService.save(addUserRequest);
            UserResponse userResponse = new UserResponse(createuser);
            return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
        }catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    //로그인 유저 마이페이지 보기
    @Operation(summary = "로그인 유저 정보", description = "유저 서비스에서 로그인 유저의 정보를 조회")
    @GetMapping("/profile/myPage")
    public ResponseEntity<Map<String, Object>> getProfileByEmail(Principal principal) throws UserPrincipalNotFoundException {
        try{
            User user = userService.findByEmail(principal.getName());
            WatchList watchList = watchListService.findByEmail(user.getEmail());

            Map<String, Object> data = new HashMap<>();
            data.put("user", new UserResponse(user));
            data.put("watchList", watchList);

            return ResponseEntity.status(HttpStatus.OK).body(data);
        } catch (Exception e){
            throw new UserPrincipalNotFoundException("인증 실패");
        }
    }

    @Operation(summary = "특정 닉네임의 유저 보기", description = "유저 서비스에서 특정 유저의 정보를 닉네임으로 조회")
    @GetMapping("/profile/nickname/{nickname}")
    public ResponseEntity<Map<String, Object>> getProfileByNickname(@PathVariable String nickname){
        User user = userService.findByNickname(nickname);
        WatchList watchList = watchListService.findByEmail(user.getEmail());

        Map<String, Object> data = new HashMap<>();
        data.put("user", new UserResponse(user));
        data.put("watchList", watchList);

        return ResponseEntity.status(HttpStatus.OK).body(data);
    }

    @Operation(summary = "특정 유저의 팔로워 목록 보기", description = "FollowService에서 실행")
    @GetMapping("/follower/{nickname}")
    public ResponseEntity<List<UserResponse>> getFollower(@PathVariable String nickname){
        List<UserResponse> list = followService.findFollowers(nickname);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @Operation(summary = "특정 유저의 팔로잉 목록 보기", description = "FollowService에서 실행")
    @GetMapping("/following/{nickname}")
    public ResponseEntity<List<UserResponse>> getFollowing(@PathVariable String nickname){
        List<UserResponse> list = followService.findFollowees(nickname);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    //현재 유저 수정
    @Operation(summary = "유저 정보 수정", description = "유저 서비스에서 현재 유저의 정보를 수정")
    @PutMapping("/update")
    public ResponseEntity<User> modifyUser(Principal principal, @RequestBody ModifyRequest modifyRequest) throws UserPrincipalNotFoundException {
        try{
            User modifyuser = userService.modify(principal.getName(), modifyRequest);
            return new ResponseEntity<>(modifyuser,HttpStatus.OK);
        } catch (Exception e){
            throw new UserPrincipalNotFoundException("인증 실패");
        }
    }

    //유저 삭제
    @Operation(summary = "유저 삭제", description = "유저 서비스에서 해당 유저를 삭제")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(Principal principal) throws UserPrincipalNotFoundException {
        try {
            userService.delete(principal.getName());
            return ResponseEntity.ok("The user was deleted.");
        } catch (Exception e) {
            throw new UserPrincipalNotFoundException("인증 실패");
        }
    }
}
