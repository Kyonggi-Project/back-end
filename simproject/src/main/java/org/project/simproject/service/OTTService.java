package org.project.simproject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.OTT;
import org.project.simproject.repository.mongoRepo.OTTRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

        ott.modifyScore(reviewCount,
                new BigDecimal(score / reviewCount).setScale(2, RoundingMode.HALF_UP).doubleValue());

        ottRepository.save(ott);
    }

    @Transactional
    public void deleteScore(OTT ott, double userScore){
        int reviewCount = ott.getReviewCount();
        double lastScore = ott.getScore();

        double score = (lastScore * reviewCount) - userScore;
        reviewCount--;

        ott.modifyScore(reviewCount,
                new BigDecimal(score / reviewCount).setScale(2, RoundingMode.HALF_UP).doubleValue());

        ottRepository.save(ott);
    }

    @Transactional
    public void reCalculationScore(OTT ott, double lastUserScore, double updateUserScore){
        int reviewCount = ott.getReviewCount();
        double lastScore = ott.getScore();

        double score = (lastScore * reviewCount) - lastUserScore + updateUserScore;

        ott.modifyScore(reviewCount,
                new BigDecimal(score / reviewCount).setScale(2, RoundingMode.HALF_UP).doubleValue());

        ottRepository.save(ott);
    }
}
