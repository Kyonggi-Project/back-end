package org.project.simproject.repository.entityRepo;

import org.project.simproject.domain.OTTReview;
import org.project.simproject.domain.OTTReviewLike;
import org.project.simproject.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OTTReviewLikeRepository extends JpaRepository<OTTReviewLike, Long> {
    boolean existsOTTReviewLikeByOttReviewIdAndUserId(OTTReview ottReview, User user);

    OTTReviewLike findOTTReviewLikeByOttReviewIdAndUserId(OTTReview ottReview, User user);
}
