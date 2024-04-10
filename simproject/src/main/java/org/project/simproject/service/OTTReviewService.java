package org.project.simproject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.OTT;
import org.project.simproject.domain.OTTReview;
import org.project.simproject.domain.User;
import org.project.simproject.dto.request.AddOTTReviewRequest;
import org.project.simproject.dto.response.OTTReviewResponse;
import org.project.simproject.repository.entityRepo.OTTReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OTTReviewService {
    private final OTTReviewRepository ottReviewRepository;

    // 추후 OTT 컨텐츠 평점 계산 기능 추가 필요
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

    // 작성한 리뷰 수정 메서드 추가(내용, 평점 등 바꿀 수 있는 컬럼 확인 필요)


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
