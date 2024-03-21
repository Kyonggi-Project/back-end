package org.project.simproject.repository;

import org.project.simproject.domain.Netflix;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NetflixRepository extends JpaRepository<Netflix, Long> {
    boolean existsNetflixByTitle(String title);
}
