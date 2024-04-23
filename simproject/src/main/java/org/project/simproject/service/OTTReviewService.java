package org.project.simproject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.OTTContents;
import org.project.simproject.domain.OTTReview;
import org.project.simproject.domain.User;
import org.project.simproject.dto.request.AddOTTReviewRequest;
import org.project.simproject.dto.request.ModifyOTTReviewRequest;
import org.project.simproject.dto.response.OTTReviewResponse;
import org.project.simproject.repository.entityRepo.OTTReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OTTReviewService {
    private final OTTReviewRepository ottReviewRepository;

    private final OTTService ottService;

    @Transactional
    public OTTReview save(User user, OTTContents ott, AddOTTReviewRequest request){
        ottService.addScore(ott, request.getScore());
        return ottReviewRepository.save(request.toEntity(user, ott.getId()));
    }

    public OTTReview findById(Long id){
        return ottReviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not Found Review"));
    }

    public List<OTTReviewResponse> findByOTTId(String ottId){
        return ottReviewRepository.findOTTReviewByOttIdOrderByLikesCountDesc(ottId)
                .stream()
                .map(OTTReviewResponse::new)
                .toList();
    }

    public List<OTTReviewResponse> findByUserId(User user){
        return ottReviewRepository.findOTTReviewByUserId(user)
                .stream()
                .map(OTTReviewResponse::new)
                .toList();
    }

    @Transactional
    public OTTReview modify(Long id, User user, ModifyOTTReviewRequest request){
        OTTReview ottReview = ottReviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not Found Review"));

        authorizeArticleAuthor(ottReview, user);

        if(ottReview.getScore() == request.getScore()) ottReview.modify(request);
        else {
            OTTContents ott = ottService.findById(ottReview.getOttId());

            ottService.reCalculationScore(ott, ottReview.getScore(), request.getScore());
            ottReview.modify(request);
        }

        return ottReview;
    }

    @Transactional
    public void delete(Long id, User user){
        OTTReview ottReview = ottReviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not Found Review"));
        OTTContents ott = ottService.findById(ottReview.getOttId());

        authorizeArticleAuthor(ottReview, user);
        ottService.deleteScore(ott, ottReview.getScore());
        ottReviewRepository.delete(ottReview);
    }

    // 현재 사용자와 리뷰 작성자가 일치하는지 검사
    private static void authorizeArticleAuthor(OTTReview ottReview, User user){
        if(!ottReview.getUserId().getNickname().equals(user.getNickname())){
            throw new IllegalArgumentException("Not Authorization User");
        }
    }
}
