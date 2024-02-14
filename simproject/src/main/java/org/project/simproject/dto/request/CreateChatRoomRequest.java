package org.project.simproject.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.simproject.domain.ChatRoom;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CreateChatRoomRequest {
    private String name;

    private String masterId;

    public ChatRoom toEntity() {
        return ChatRoom.builder()
                .name(name)
                .masterId(masterId)
                .build();
    }
}
