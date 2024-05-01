package org.project.simproject.repository.mongoRepo;

import org.project.simproject.domain.OTTContents;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OTTContentsRepository extends MongoRepository<OTTContents, String> {
    OTTContents findOTTByTitle(String title);
    OTTContents findOTTContentsByTitleAndPosterImg(String title, String posterImg);
    boolean existsOTTByTitle(String title);
    boolean existsOTTContentsByTitleAndPosterImg(String title, String posterImg);
}
