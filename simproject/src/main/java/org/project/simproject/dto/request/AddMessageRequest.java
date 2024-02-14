package org.project.simproject.dto.request;

import lombok.*;
import org.project.simproject.domain.Message;


@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AddMessageRequest {
    private String content;

    private Long roomId;

    private String sender;

    public Message toEntity() {
        return Message.builder()
                .content(content)
                .roomId(roomId)
                .sender(sender)
                .build();
    }
}
