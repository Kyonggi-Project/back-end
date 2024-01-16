package org.project.simproject.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.simproject.domain.User;

@Getter
@NoArgsConstructor
public class UserResponse {
    private String nickname;
    private int articlesCount;

    public UserResponse(User user){
        this.nickname = user.getNickname();
        this.articlesCount = user.getArticlesCount();
    }
}
