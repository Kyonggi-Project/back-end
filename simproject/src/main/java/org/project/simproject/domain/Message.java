package org.project.simproject.domain;

import jakarta.persistence.*;
import lombok.*;
import org.project.simproject.util.ChatMessageStatus;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private Long roomId;

    private String sender;

    private LocalDateTime sendDateTime;

    @Enumerated(EnumType.STRING)
    private ChatMessageStatus status;

    @Builder
    public Message(String content, Long roomId, String sender, ChatMessageStatus status) {
        this.content = content;
        this.roomId = roomId;
        this.sender = sender;
        this.status = status;
        this.sendDateTime = LocalDateTime.now();
    }
}
