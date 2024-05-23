package org.project.simproject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.OTTContents;
import org.project.simproject.dto.response.SentimentResponseDTO;
import org.project.simproject.repository.mongoRepo.OTTContentsRepository;
import org.project.simproject.util.OTTContentsCustomRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OTTService {
    private final OTTContentsRepository ottRepository;

    private final OTTContentsCustomRepository ottContentsCustomRepository;

    private final SentimentAnalysisService analysisService;

    public OTTContents findById(String id){
        return ottRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not Found Contents"));
    }

    @Transactional
    public void addRating(OTTContents ottContents, List<String> tags) throws IOException {
        StringBuilder builder = new StringBuilder();
        float magnitude;
        float score;
        float result;

        for(String tag : tags){
            builder.append(tag + ". ");
        }

        String tag = builder.toString();
        SentimentResponseDTO response = analysisService.analyzeSentiment(tag);
        magnitude = response.getMagnitude();
        score = response.getScore();
        result = reCalculationRating(magnitude, score);

        ottContents.modifyRating(result);

        ottRepository.save(ottContents);

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

    public void initializeRatingCount() {
        ottContentsCustomRepository.initializeRatingCount();
    }

    public float reCalculationRating(float magnitude, float score){
        String mag = "1.0";
        if(magnitude > 1.0f){
            magnitude-=score;
            int val = (int)magnitude;
            magnitude-=val;
            magnitude*=10;

            String rat = mag + String.valueOf((int)magnitude);
            float rating = Float.valueOf(rat);

            score*=rating;
        }
        return score;
    }
}
