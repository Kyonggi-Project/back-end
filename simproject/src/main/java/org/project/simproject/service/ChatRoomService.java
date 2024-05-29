package org.project.simproject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.ChatRoom;
import org.project.simproject.domain.User;
import org.project.simproject.dto.request.CreateChatRoomRequest;
import org.project.simproject.dto.response.ChatRoomResponse;
import org.project.simproject.repository.entityRepo.ChatRoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public ChatRoomResponse save(CreateChatRoomRequest createChatRoomRequest, User user) {
        return new ChatRoomResponse(chatRoomRepository.save(createChatRoomRequest.toEntity(user.getNickname())));
    }

    public List<ChatRoomResponse> findAll() {
        return chatRoomRepository.findAll()
                .stream()
                .map(ChatRoomResponse::new)
                .toList();
    }

    public List<ChatRoomResponse> findByName(String name) {
        return chatRoomRepository.findAllByNameContains(name)
                .stream()
                .map(ChatRoomResponse::new)
                .toList();
    }

    @Transactional
    public ChatRoomResponse modify(String name, Long roomId, String userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("ChatRoom Not Found"));

        if(chatRoom.getMasterId().equals(userId)) {
            chatRoom.modify(name);
        }

        return new ChatRoomResponse(chatRoom);
    }

    @Transactional
    public void delete(Long roomId, String userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                        .orElseThrow(() -> new IllegalArgumentException("ChatRoom Not Found"));
        if(chatRoom.getMasterId().equals(userId)) {
            chatRoomRepository.delete(chatRoom);
        }
        else {
            throw new IllegalArgumentException("User Not Authentication");
        }
    }
}
