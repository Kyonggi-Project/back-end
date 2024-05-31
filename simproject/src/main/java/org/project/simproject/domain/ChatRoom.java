package org.project.simproject.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chat_rooms")
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String masterId;

    private int memberCount;

    public void modify(String name) {
        this.name = name;
    }

    public void addMember(){
        this.memberCount++;
    }

    public void exitMember(){
        this.memberCount--;
    }

    @Builder
    public ChatRoom(String name, String masterId) {
        this.name = name;
        this.masterId = masterId;
        this.memberCount = 0;
    }
}
