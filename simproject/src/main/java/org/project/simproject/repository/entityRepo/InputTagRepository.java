package org.project.simproject.repository.entityRepo;

import org.project.simproject.domain.InputTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InputTagRepository extends JpaRepository<InputTag, Long> {
    boolean existsInputTagByName(String name);
}
