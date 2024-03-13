package org.project.simproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.User;
import org.project.simproject.dto.request.CreateChatRoomRequest;
import org.project.simproject.dto.response.ChatRoomResponse;
import org.project.simproject.service.ChatRoomService;
import org.project.simproject.service.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatroom")
@Tag(name = "대화방", description = "대화방 기능")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;
    private final TokenService tokenService;

    @Operation(summary = "대화방 만들기", description = "대화방 서비스에서 데이터베이스에 대화방 데이터 추가")
    @PostMapping("/addChatroom")
    public ResponseEntity<ChatRoomResponse> addChatRoom(@RequestBody CreateChatRoomRequest createChatRoomRequest,
                                                        @CookieValue(value = "refresh_token", defaultValue = "cookie") String cookie) {
        User user = tokenService.findByUserId(cookie);
        return ResponseEntity.status(HttpStatus.CREATED).body(chatRoomService.save(createChatRoomRequest, user));
    }

    @Operation(summary = "모든 대화방 찾기", description = "대화방 서비스에서 데이터베이스에서 모든 대화방 데이터 찾기")
    @GetMapping("/allChatrooms")
    public ResponseEntity<List<ChatRoomResponse>> findAllChatRooms() {
        return ResponseEntity.status(HttpStatus.OK).body(chatRoomService.findAll());
    }

    @Operation(summary = "대화방 찾기", description = "대화방 서비스에서 데이터베이스에서 이름으로 대화방 데이터 찾기")
    @GetMapping("/viewChatroom")
    public ResponseEntity<List<ChatRoomResponse>> findChatRoomsByName(@RequestParam String name) {
        return ResponseEntity.status(HttpStatus.OK).body(chatRoomService.findByName(name));
    }

    @Operation(summary = "대화방 이름 수정", description = "대화방 서비스에서 데이터베이스에서 이름을 수정")
    @PutMapping("/update/{roomId}")
    public ResponseEntity<ChatRoomResponse> modifyChatRoom(@PathVariable Long roomId, @RequestBody String name, @RequestParam String userId) {
        return ResponseEntity.status(HttpStatus.OK).body(chatRoomService.modify(name, roomId, userId));
    }

    @Operation(summary = "대화방 삭제", description = "대화방 서비스에서 데이터베이스에서 대화방 삭제")
    @DeleteMapping("/delete/{roomId}")
    public ResponseEntity<Void> deleteChatRoom(@PathVariable Long roomId, @RequestParam String userId) {
        chatRoomService.delete(roomId, userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
