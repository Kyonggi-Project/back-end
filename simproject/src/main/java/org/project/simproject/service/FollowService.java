package org.project.simproject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.Follow;
import org.project.simproject.domain.User;
import org.project.simproject.dto.response.UserResponse;
import org.project.simproject.repository.entityRepo.FollowRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final UserService userService;

    @Transactional
    public void toggle(Long id, String followeeEmail){
        User follower = userService.findById(id);
        User followee = userService.findByEmail(followeeEmail);

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

    public List<UserResponse> findFollowers(String nickname){
        User user = userService.findByNickname(nickname);
        return user.getFollowers()
                .stream()
                .map(Follow::getFollower)
                .map(UserResponse::new)
                .toList();
    }

    public List<UserResponse> findFollowees(String nickname){
        User user = userService.findByNickname(nickname);
        return user.getFollowing()
                .stream()
                .map(Follow::getFollowee)
                .map(UserResponse::new)
                .toList();
    }

    public boolean isFollowed(User follower, User followee){
        return followRepository.existsFollowByFollowerAndFollowee(follower, followee);
    }
}
