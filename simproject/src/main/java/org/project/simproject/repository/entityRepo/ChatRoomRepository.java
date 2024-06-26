package org.project.simproject.repository.entityRepo;

import org.project.simproject.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findAllByNameContains(String name);
}
