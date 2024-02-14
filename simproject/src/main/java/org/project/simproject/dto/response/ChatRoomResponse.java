package org.project.simproject.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.simproject.domain.ChatRoom;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChatRoomResponse {
    private String name;

    private String masterId;

    public ChatRoomResponse(ChatRoom chatRoom) {
        this.name = chatRoom.getName();
        this.masterId = chatRoom.getMasterId();
    }
}
