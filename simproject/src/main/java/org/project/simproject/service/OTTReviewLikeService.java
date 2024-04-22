package org.project.simproject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.OTTReview;
import org.project.simproject.domain.OTTReviewLike;
import org.project.simproject.domain.User;
import org.project.simproject.repository.entityRepo.OTTReviewLikeRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OTTReviewLikeService {
    private final OTTReviewLikeRepository ottReviewLikeRepository;

    private final OTTReviewService ottReviewService;

    @Transactional
    public void toggle(Long ottReviewId, User user){
        OTTReview ottReview = ottReviewService.findById(ottReviewId);

        if(isLiked(ottReview, user)){
            OTTReviewLike deleteLike = ottReviewLikeRepository.findOTTReviewLikeByOttReviewIdAndUserId(ottReview, user);

            ottReview.deleteLike();
            ottReviewLikeRepository.delete(deleteLike);
        } else{
            OTTReviewLike ottReviewLike = OTTReviewLike.builder()
                    .ottReviewId(ottReview)
                    .userId(user)
                    .build();

            ottReview.addLike();
            ottReviewLikeRepository.save(ottReviewLike);
        }
    }

    public boolean isLiked(OTTReview ottReview, User user){
        return ottReviewLikeRepository.existsOTTReviewLikeByOttReviewIdAndUserId(ottReview, user);
    }
}
