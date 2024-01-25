package org.project.simproject.service;

import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.Follow;
import org.project.simproject.domain.User;
import org.project.simproject.repository.FollowRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;

    public void toggle(User follower, User followee){
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

    public boolean isFollowed(User follower, User followee){
        return followRepository.existsFollowByFollowerAndFollowee(follower, followee);
    }
}
