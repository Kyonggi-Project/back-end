package org.project.simproject.repository.mongoRepo;

import org.project.simproject.domain.Movie;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MovieRepository extends MongoRepository<Movie, String> {
    Movie findByTitle(String title);
    boolean existsByTitle(String title);
}
