package org.project.simproject.repository.entityRepo;

import org.project.simproject.domain.OTTReview;
import org.project.simproject.domain.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {
    List<Reply> findReplyByOttReviewId(OTTReview ottReview);
}
