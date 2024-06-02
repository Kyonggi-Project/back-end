package org.project.simproject.service;

import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.OTTContents;
import org.project.simproject.util.OTTContentsCustomRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.concurrent.CountDownLatch;

@Repository
@RequiredArgsConstructor
public class OTTContentsCustomRepositoryImpl implements OTTContentsCustomRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public void initializeRankingScore() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Query query = new Query();
        Update update = new Update().set("rankingScore", 0);
        mongoTemplate.updateMulti(query, update, OTTContents.class).getModifiedCount();

        latch.countDown();

        latch.await();
    }

    @Override
    public void initializeRatingCount() {
        Query query = new Query();
        Update update = new Update().set("ratingCount", 0);
        mongoTemplate.updateMulti(query, update, OTTContents.class);
    }
}
