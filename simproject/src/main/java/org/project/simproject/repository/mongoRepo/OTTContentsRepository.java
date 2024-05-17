package org.project.simproject.repository.mongoRepo;

import org.project.simproject.domain.OTTContents;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OTTContentsRepository extends MongoRepository<OTTContents, String> {
    OTTContents findOTTByTitle(String title);
    OTTContents findOTTContentsByHref(String href);
    OTTContents findOTTContentsByTitleContaining(String prefixBeforeName);
    OTTContents findOTTContentsBySubtitleListContaining(String replace);

    List<OTTContents> findAllByGenreListContainsIgnoreCase(String genre);
    List<OTTContents> findAllByTitle(String title);
    List<OTTContents> findAllByTitleContainsIgnoreCaseAndSubtitleListContainsIgnoreCase(String title, String subtitle);
    List<OTTContents> findAllBySubtitleListContainsIgnoreCase(String subtitle);
    List<OTTContents> findAllByRatingBetween(float minRating, float maxRating);
    List<OTTContents> findAllOTTContentsByTitle(String ottTitle);

    boolean existsOTTByTitle(String title);
    boolean existsBySubtitleListContainsIgnoreCase(String subtitle);
    boolean existsOTTContentsByHref(String href);
}
