package org.project.simproject.controller;

import lombok.RequiredArgsConstructor;
import org.project.simproject.dto.response.MessageResponse;
import org.project.simproject.service.MessageService;
import org.project.simproject.util.ChatMessageStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MessageRestController {    // axios를 사용하여 메시지를 동기화하기 위한 RestAPIController
    private final MessageService messageService;

    @GetMapping("/chat/messages/{roomId}/{loginId}")    // 특정 유저의 LEAVE 메시지의 개수 추가로 반환
    public Map<String, Object> getMessageList(@PathVariable Long roomId, @PathVariable String loginId){
        Map<String, Object> responseData = new HashMap<>();
        List<MessageResponse> list = messageService.findAllByRoomId(roomId);
        int count = messageService.findMessageByRoomIdAndSenderAndStatus(roomId, loginId, ChatMessageStatus.LEAVE);
        responseData.put("list", list);
        responseData.put("count", count);
        return responseData;
    }
}
