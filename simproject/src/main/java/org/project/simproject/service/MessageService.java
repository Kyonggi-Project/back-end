package org.project.simproject.service;

import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.Message;
import org.project.simproject.dto.request.AddMessageRequest;
import org.project.simproject.dto.response.MessageResponse;
import org.project.simproject.repository.MessageRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageResponse save(AddMessageRequest addMessageRequest) {
        return new MessageResponse(messageRepository.save(addMessageRequest.toEntity()));
    }

    public void delete(Long messageId, String userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message Not Found"));
        if(message.getSender().equals(userId)) {
            messageRepository.delete(message);
        }
    }
}
