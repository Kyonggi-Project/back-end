package org.project.simproject.repository.mongoRepo;

import org.project.simproject.domain.OTTContents;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OTTContentsRepository extends MongoRepository<OTTContents, String> {
    OTTContents findOTTByTitle(String title);
    List<OTTContents> findAllByGenreListContainsIgnoreCase(String genre);
    List<OTTContents> findAllByTitle(String title);
    List<OTTContents> findAllByTitleContainsIgnoreCaseAndSubtitleListContainsIgnoreCase(String title, String subtitle);
    OTTContents findOTTContentsByHref(String href);
    boolean existsOTTByTitle(String title);
    boolean existsOTTContentsByHref(String href);
}
