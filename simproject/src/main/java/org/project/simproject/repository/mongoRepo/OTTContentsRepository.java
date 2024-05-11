package org.project.simproject.repository.mongoRepo;

import org.project.simproject.domain.OTTContents;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OTTContentsRepository extends MongoRepository<OTTContents, String> {
    OTTContents findOTTByTitle(String title);
    List<OTTContents> findAllByGenreListContainsIgnoreCase(String genre);
    OTTContents findOTTContentsByTitleAndPosterImg(String title, String posterImg);
    List<OTTContents> findAllByTitle(String title);
    List<OTTContents> findAllByTitleContainsIgnoreCaseAndSubtitleListContainsIgnoreCase(String title, String subtitle);
    boolean existsOTTByTitle(String title);
    boolean existsOTTContentsByTitleAndPosterImg(String title, String posterImg);

    List<OTTContents> findAllOTTContentsByTitle(String ottTitle);

    OTTContents findOTTContentsBySubtitleListContaining(String replace);

    OTTContents findOTTContentsByTitleContaining(String prefixBeforeName);
}
