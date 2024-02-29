package org.project.simproject.service;

import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.Message;
import org.project.simproject.dto.request.AddMessageRequest;
import org.project.simproject.dto.response.MessageResponse;
import org.project.simproject.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageResponse save(AddMessageRequest addMessageRequest) {
        return new MessageResponse(messageRepository.save(addMessageRequest.toEntity()));
    }

    // 특정 채팅방의 메시지를 모두 불러와 DTO로 변환해 Controller로 전달
    public List<MessageResponse> findAllByRoomId(Long roomId){
        List<MessageResponse> list = messageRepository.findAllByRoomId(roomId)
                .stream()
                .map(MessageResponse::new)
                .toList();
        return list;
    }

    public void delete(Long messageId, String userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message Not Found"));
        if(message.getSender().equals(userId)) {
            messageRepository.delete(message);
        }
    }

    // 이미 구독중인 채팅방인지 참/거짓 판단
    public boolean isSubscribed(String sender, Long roomId){
        return messageRepository.existsMessageBySenderAndRoomId(sender, roomId);
    }
}
