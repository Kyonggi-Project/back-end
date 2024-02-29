package org.project.simproject.repository;

import org.project.simproject.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllByRoomId(Long roomId);
    boolean existsMessageBySenderAndRoomId(String sender, Long roomId);
}
