package org.project.simproject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.Follow;
import org.project.simproject.domain.User;
import org.project.simproject.dto.response.UserResponse;
import org.project.simproject.repository.entityRepo.FollowRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final UserService userService;

    @Transactional
    public void toggle(String email, String nickname){
        User follower = userService.findByEmail(email);
        User followee = userService.findByNickname(nickname);

        if(isFollowed(follower, followee)){
            Follow follow = followRepository.findFollowByFollowerAndFollowee(follower, followee);
            followRepository.delete(follow);
        }
        else{
            Follow follow = Follow.builder()
                    .follower(follower)
                    .followee(followee)
                    .build();
            followRepository.save(follow);
        }
    }

    public List<UserResponse> findFollowers(String nickname, String email){
        User user = userService.findByNickname(nickname);
        User loginUser = userService.findByEmail(email);
        List<Follow> followers = user.getFollowers();

        List<UserResponse> list = new ArrayList<>();
        for(Follow follow : followers){
            boolean isFollowed = isFollowed(loginUser, follow.getFollower());
            list.add(new UserResponse(follow.getFollower(), isFollowed));
        }

        return list;
    }

    public List<UserResponse> findFollowees(String nickname, String email){
        User user = userService.findByNickname(nickname);
        User loginUser = userService.findByEmail(email);
        List<Follow> followees = user.getFollowing();

        List<UserResponse> list = new ArrayList<>();
        for(Follow follow : followees){
            boolean isFollowed = isFollowed(loginUser, follow.getFollowee());
            list.add(new UserResponse(follow.getFollowee(), isFollowed));
        }

        return list;
    }

    public boolean isFollowed(User follower, User followee){
        return followRepository.existsFollowByFollowerAndFollowee(follower, followee);
    }
}
