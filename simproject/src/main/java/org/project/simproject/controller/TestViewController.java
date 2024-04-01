package org.project.simproject.controller;

import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.ChatRoom;
import org.project.simproject.domain.User;
import org.project.simproject.dto.request.AddUserRequest;
import org.project.simproject.dto.response.ChatRoomResponse;
import org.project.simproject.dto.response.UserResponse;
import org.project.simproject.repository.entityRepo.ChatRoomRepository;
import org.project.simproject.service.ChatRoomService;
import org.project.simproject.service.TokenService;
import org.project.simproject.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class TestViewController {
    private final UserService userService;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomService chatRoomService;
    private final TokenService tokenService;

    @PostMapping("/signup")
    public String save(AddUserRequest addUserRequest){
        userService.save(addUserRequest);
        return "redirect:/login";
    }

    @GetMapping("/oauthLogin")
    public String oauthLogin(){
        return "oauthLogin";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/signup")
    public String signUp(){
        return "signup";
    }

    // 특정 채팅방 입장을 위한 GET Method
    @GetMapping("/chat/room/{roomId}")
    public String enterChatRoom(Model model, @PathVariable Long roomId,
                                @CookieValue(value = "refresh_token", defaultValue = "cookie") String cookie){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("not found"));
        User user = tokenService.findByUserId(cookie);
        model.addAttribute("room", new ChatRoomResponse(chatRoom));
        model.addAttribute("user", new UserResponse(user));

        return "room";
    }

    // 모든 채팅방의 리스트를 보여주기 위한 GET Method
    @GetMapping("/chat/room/list")
    public String chatRoomList(Model model){
        List<ChatRoomResponse> list = chatRoomService.findAll();
        model.addAttribute("room", list);

        return "chatRoomList";
    }
}
