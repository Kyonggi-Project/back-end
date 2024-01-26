package org.project.simproject.repository;

import org.project.simproject.domain.Follow;
import org.project.simproject.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Follow findFollowByFollowerAndFollowee(User follower, User followee);
    boolean existsFollowByFollowerAndFollowee(User follower, User followee);
}
