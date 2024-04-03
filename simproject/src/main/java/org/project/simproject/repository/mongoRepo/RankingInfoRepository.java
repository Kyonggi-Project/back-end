package org.project.simproject.repository.mongoRepo;

import org.project.simproject.domain.RankingInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RankingInfoRepository extends MongoRepository<RankingInfo, String> {
}
