package org.project.simproject.repository.mongoRepo;

import org.project.simproject.domain.OTTContents;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OTTContentsRepository extends MongoRepository<OTTContents, String> {
    OTTContents findOTTByTitle(String title);
    boolean existsOTTByTitle(String title);
}
