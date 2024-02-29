package org.project.simproject.repository;

import org.project.simproject.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllByRoomId(Long roomId);     // 특정 채팅방의 모든 메시지를 불러옴
    boolean existsMessageBySenderAndRoomId(String sender, Long roomId);     // 특정 sender가 채팅방에 입장한 적이 있는지 bool 판단
}
