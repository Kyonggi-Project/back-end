package org.project.simproject.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.simproject.domain.Message;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MessageResponse {
    private String content;

    private String sender;

    private LocalDateTime sendDateTime;

    public MessageResponse(Message message) {
        this.content = message.getContent();
        this.sender = message.getSender();
        this.sendDateTime = message.getSendDateTime();
    }
}
