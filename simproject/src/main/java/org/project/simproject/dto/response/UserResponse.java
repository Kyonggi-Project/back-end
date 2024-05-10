package org.project.simproject.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.simproject.domain.User;

@Getter
@NoArgsConstructor
public class UserResponse {
    private String nickname;
    private int followers;
    private int following;
    private boolean isFollowed;

    public UserResponse(User user){
        this.nickname = user.getNickname();
        this.followers = user.getFollowers().size();
        this.following = user.getFollowing().size();
    }

    public UserResponse(User user, boolean isFollowed){
        this.nickname = user.getNickname();
        this.followers = user.getFollowers().size();
        this.following = user.getFollowing().size();
        this.isFollowed = isFollowed;
    }
}
