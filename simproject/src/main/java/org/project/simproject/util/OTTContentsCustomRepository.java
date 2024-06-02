package org.project.simproject.util;

public interface OTTContentsCustomRepository {
    void initializeRankingScore() throws InterruptedException;
    void initializeRatingCount();
}
