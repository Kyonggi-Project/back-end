package org.project.simproject.domain;

import jakarta.persistence.*;
import lombok.*;

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

    @Builder
    public Message(String content, Long roomId, String sender) {
        this.content = content;
        this.roomId = roomId;
        this.sender = sender;
        this.sendDateTime = LocalDateTime.now();
    }
}
