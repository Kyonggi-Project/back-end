package org.project.simproject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.OTT;
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

    // 추후 OTT 컨텐츠 평점을 재계산하는 코드 추가 예정
    @Transactional
    public OTTReview save(User user, OTT ott, AddOTTReviewRequest request){
        return ottReviewRepository.save(request.toEntity(user, ott.getId()));
    }

    public OTTReview findById(Long id){
        return ottReviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not Found Review"));
    }

    public List<OTTReviewResponse> findByOTTId(OTT ott){
        return ottReviewRepository.findOTTReviewByOttIdOrderByScore(ott.getId())
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

    // 추후 평점을 변경했을 시, 컨텐츠 평점을 재계산하는 코드 추가 예정
    @Transactional
    public OTTReview modify(Long id, User user, ModifyOTTReviewRequest request){
        OTTReview ottReview = ottReviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not Found Review"));

        authorizeArticleAuthor(ottReview, user);
        ottReview.modify(request);

        return ottReview;
    }

    @Transactional
    public void delete(Long id, User user){
        OTTReview ottReview = ottReviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not Found Review"));

        authorizeArticleAuthor(ottReview, user);
        ottReviewRepository.delete(ottReview);
    }

    // 현재 사용자와 리뷰 작성자가 일치하는지 검사
    private static void authorizeArticleAuthor(OTTReview ottReview, User user){
        if(!ottReview.getUserId().getNickname().equals(user.getNickname())){
            throw new IllegalArgumentException("Not Authorization User");
        }
    }
}
