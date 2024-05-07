package org.project.simproject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.OTTContents;
import org.project.simproject.repository.mongoRepo.OTTContentsRepository;
import org.project.simproject.util.OTTContentsCustomRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class OTTService {
    private final OTTContentsRepository ottRepository;

    private final OTTContentsCustomRepository ottContentsCustomRepository;

    public OTTContents findById(String id){
        return ottRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not Found Contents"));
    }

    public OTTContents findByTitle(String title){
        return ottRepository.findOTTByTitle(title);
    }

    @Transactional
    public void addScore(OTTContents ott, double userScore){
        int reviewCount = ott.getReviewCount();
        double lastScore = ott.getScore();

        double score = (lastScore * reviewCount) + userScore;
        reviewCount++;

        ott.modifyScore(reviewCount,
                new BigDecimal(score / reviewCount).setScale(2, RoundingMode.HALF_UP).doubleValue());

        ottRepository.save(ott);
    }

    @Transactional
    public void deleteScore(OTTContents ott, double userScore){
        int reviewCount = ott.getReviewCount();
        double lastScore = ott.getScore();

        double score = (lastScore * reviewCount) - userScore;
        reviewCount--;

        if(score == 0.0) ott.modifyScore(reviewCount, 0.0);
        else{
            ott.modifyScore(reviewCount,
                    new BigDecimal(score / reviewCount).setScale(2, RoundingMode.HALF_UP).doubleValue());
        }

        ottRepository.save(ott);
    }

    @Transactional
    public void reCalculationScore(OTTContents ott, double lastUserScore, double updateUserScore){
        int reviewCount = ott.getReviewCount();
        double lastScore = ott.getScore();

        double score = (lastScore * reviewCount) - lastUserScore + updateUserScore;

        ott.modifyScore(reviewCount,
                new BigDecimal(score / reviewCount).setScale(2, RoundingMode.HALF_UP).doubleValue());

        ottRepository.save(ott);
    }

    public void initializeRankingScore() {
        ottContentsCustomRepository.initializeRankingScore();
    }
}
