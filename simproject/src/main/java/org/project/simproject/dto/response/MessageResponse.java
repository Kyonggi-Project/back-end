package org.project.simproject.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.simproject.domain.Message;
import org.project.simproject.util.ChatMessageStatus;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MessageResponse {
    private String content;

    private String sender;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime sendDateTime;

    private ChatMessageStatus status;

    public MessageResponse(Message message) {
        this.content = message.getContent();
        this.sender = message.getSender();
        this.sendDateTime = message.getSendDateTime();
        this.status = message.getStatus();
    }
}
