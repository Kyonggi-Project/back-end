package org.project.simproject.controller;

import lombok.RequiredArgsConstructor;
import org.project.simproject.dto.request.AddMessageRequest;
import org.project.simproject.dto.response.MessageResponse;
import org.project.simproject.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final SimpMessagingTemplate template;

    @MessageMapping("/enter/{roomId}")
    public void enterChatRoom(AddMessageRequest addMessageRequest) {
        if(!messageService.isSubscribed(addMessageRequest.getSender(), addMessageRequest.getRoomId())){
            addMessageRequest.setContent(addMessageRequest.getSender() + "님이 입장하였습니다.");
            template.convertAndSend("/topic/" + addMessageRequest.getRoomId(),
                    messageService.save(addMessageRequest));
        }
        else{
            template.convertAndSend("/topic/" + addMessageRequest.getRoomId(), addMessageRequest);
        }
    }

    @MessageMapping("/send/{roomId}")
    @SendTo("/topic/{roomId}")
    public MessageResponse sendMessage(AddMessageRequest addMessageRequest) {
        return messageService.save(addMessageRequest);
    }

    /*@DeleteMapping("/delete/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long messageId, @RequestParam String userId) {
        messageService.delete(messageId, userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }*/
}
