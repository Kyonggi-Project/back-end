package org.project.simproject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.simproject.domain.OTTContents;
import org.project.simproject.repository.mongoRepo.OTTContentsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OTTContentsService {

    private final OTTContentsRepository ottContentsRepository;

    public List<OTTContents> getTop10Contents() {
        return ottContentsRepository.findAll().subList(0, 10);
    }

    public List<OTTContents> get20ContentsByGenre(String genre) {
        return ottContentsRepository.findAll().subList(0, 20);
    }

}
