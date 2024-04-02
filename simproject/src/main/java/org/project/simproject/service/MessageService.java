package org.project.simproject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.simproject.dto.request.AddMessageRequest;
import org.project.simproject.dto.response.MessageResponse;
import org.project.simproject.repository.entityRepo.MessageRepository;
import org.project.simproject.util.ChatMessageStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    @Transactional
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

    // 특정 메시지 타입을 가지는 채팅 메시지의 개수를 돌려줌
    public int findMessageByRoomIdAndSenderAndStatus(Long roomId, String sender, ChatMessageStatus status){
        return messageRepository.findMessageByRoomIdAndSenderAndStatus(roomId, sender, status).size();
    }

    /*public void delete(Long messageId, String userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message Not Found"));
        if(message.getSender().equals(userId)) {
            messageRepository.delete(message);
        }
    }*/
}
