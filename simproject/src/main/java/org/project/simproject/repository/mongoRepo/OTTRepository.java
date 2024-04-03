package org.project.simproject.repository.mongoRepo;

import org.project.simproject.domain.OTT;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OTTRepository extends MongoRepository<OTT, String> {

}
