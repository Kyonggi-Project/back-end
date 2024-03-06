package org.project.simproject.repository;

import org.project.simproject.domain.Message;
import org.project.simproject.util.ChatMessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllByRoomId(Long roomId);     // 특정 채팅방의 모든 메시지를 불러옴

    // 특정 메시지 타입을 가지는 채팅 메시지 리스트를 불러옴
    List<Message> findMessageByRoomIdAndSenderAndStatus(Long roomId, String sender, ChatMessageStatus status);
}
