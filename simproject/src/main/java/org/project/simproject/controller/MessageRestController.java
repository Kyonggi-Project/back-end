package org.project.simproject.controller;

import lombok.RequiredArgsConstructor;
import org.project.simproject.dto.response.MessageResponse;
import org.project.simproject.service.MessageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MessageRestController {
    private final MessageService messageService;

    @GetMapping("/chat/messages/{roomId}")
    public List<MessageResponse> getMessage(@PathVariable Long roomId){
        List<MessageResponse> list = messageService.findAllByRoomId(roomId);
        return list;
    }
}
