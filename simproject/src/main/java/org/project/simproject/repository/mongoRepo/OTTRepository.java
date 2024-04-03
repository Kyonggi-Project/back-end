package org.project.simproject.repository.mongoRepo;

import org.project.simproject.domain.OTT;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OTTRepository extends MongoRepository<OTT, String> {
    OTT findOTTByTitle(String title);
    boolean existsOTTByTitle(String title);
}
