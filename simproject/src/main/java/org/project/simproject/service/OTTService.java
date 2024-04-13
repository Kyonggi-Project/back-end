package org.project.simproject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.OTT;
import org.project.simproject.repository.mongoRepo.OTTRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OTTService {
    private final OTTRepository ottRepository;

    public OTT findById(String id){
        return ottRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not Found Contents"));
    }

    public OTT findByTitle(String title){
        return ottRepository.findOTTByTitle(title);
    }

    @Transactional
    public void addScore(OTT ott, double userScore){
        int reviewCount = ott.getReviewCount();
        double lastScore = ott.getScore();

        double score = (lastScore * reviewCount) + userScore;
        reviewCount++;

        ott.modifyScore(reviewCount, score);

        ottRepository.save(ott);
    }

    @Transactional
    public void reCalculationScore(OTT ott, double lastUserScore, double updateUserScore){
        int reviewCount = ott.getReviewCount();
        double lastScore = ott.getScore();

        double score = (lastScore * reviewCount) - lastUserScore + updateUserScore;

        ott.modifyScore(reviewCount, score);

        ottRepository.save(ott);
    }
}
