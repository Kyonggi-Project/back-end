package org.project.simproject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.OTTContents;
import org.project.simproject.domain.OTTReview;
import org.project.simproject.domain.User;
import org.project.simproject.dto.request.AddOTTReviewRequest;
import org.project.simproject.dto.request.ModifyOTTReviewRequest;
import org.project.simproject.dto.response.OTTReviewResponse;
import org.project.simproject.repository.entityRepo.OTTReviewLikeRepository;
import org.project.simproject.repository.entityRepo.OTTReviewRepository;
import org.project.simproject.repository.entityRepo.UserRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OTTReviewService {
    private final OTTReviewRepository ottReviewRepository;

    private final OTTService ottService;

    private final OTTReviewLikeRepository ottReviewLikeRepository;

    private final UserService userService;

    @Transactional
    public OTTReview save(User user, OTTContents ott, AddOTTReviewRequest request) throws IOException {
        ottService.addScore(ott, request.getScore());
        //ottService.addRating(ott, request.getTags());
        return ottReviewRepository.save(request.toEntity(user, ott.getId(), ott.getTitle(), ott.getPosterImg()));
    }

    public OTTReview findById(Long id){
        return ottReviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not Found Review"));
    }

    public OTTReviewResponse findByIdForDTO(Long id, User user){
        OTTReview ottReview = ottReviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not Found Review"));
        OTTContents ottContents = ottService.findById(ottReview.getOttId());
        boolean isLiked = ottReviewLikeRepository.existsOTTReviewLikeByOttReviewIdAndUserId(ottReview, user);

        return new OTTReviewResponse(ottReview, ottContents, isLiked);
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
    public OTTReview modify(Long id, ModifyOTTReviewRequest request){
        OTTReview ottReview = ottReviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not Found Review"));


        if(ottReview.getScore() == request.getScore()) ottReview.modify(request);
        else {
            OTTContents ott = ottService.findById(ottReview.getOttId());

            ottService.reCalculationScore(ott, ottReview.getScore(), request.getScore());
            ottReview.modify(request);
        }

        return ottReview;
    }

    @Transactional
    public void delete(Long id){
        OTTReview ottReview = ottReviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not Found Review"));
        OTTContents ott = ottService.findById(ottReview.getOttId());

        ottService.deleteScore(ott, ottReview.getScore());
        ottReviewRepository.delete(ottReview);
    }

    public boolean existsOTTReview(String ottId, String email){
        User user = userService.findByEmail(email);

        return ottReviewRepository.existsOTTReviewByOttIdAndUserId(ottId, user);
    }
}
