package org.project.simproject.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.simproject.domain.User;

@Getter
@NoArgsConstructor
public class UserResponse {
    private String nickname;
    private int articlesCount;
    private int followers;
    private int following;

    public UserResponse(User user){
        this.nickname = user.getNickname();
        this.articlesCount = user.getArticlesCount();
        this.followers = user.getFollowers().size();
        this.following = user.getFollowing().size();
    }
}
