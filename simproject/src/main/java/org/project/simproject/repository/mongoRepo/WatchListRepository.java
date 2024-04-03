package org.project.simproject.repository.mongoRepo;

import org.project.simproject.domain.WatchList;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WatchListRepository extends MongoRepository<WatchList, String> {

}
