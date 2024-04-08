package org.project.simproject.service;

import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.WatchList;
import org.project.simproject.repository.mongoRepo.WatchListRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WatchListService {
    private final WatchListRepository watchListRepository;

    public WatchList findByEmail(String email){
        return watchListRepository.findWatchListByEmail(email);
    }
}
