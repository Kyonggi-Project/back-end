package org.project.simproject.repository.entityRepo;

import org.project.simproject.domain.OTTReview;
import org.project.simproject.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OTTReviewRepository extends JpaRepository<OTTReview, Long> {
    List<OTTReview> findOTTReviewByOttIdOrderByScore(String ottId);

    List<OTTReview> findOTTReviewByUserId(User user);
}
