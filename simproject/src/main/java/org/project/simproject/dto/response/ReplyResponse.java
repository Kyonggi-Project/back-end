package org.project.simproject.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.simproject.domain.Reply;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReplyResponse {
    private Long id;

    private String nickname;

    private String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime updatedAt;

    public ReplyResponse(Reply reply){
        this.id = reply.getId();
        this.nickname = reply.getUserId().getNickname();
        this.content = reply.getContent();
        this.updatedAt = reply.getUpdatedAt();
    }
}
