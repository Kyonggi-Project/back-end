package org.project.simproject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.OTT;
import org.project.simproject.domain.WatchList;
import org.project.simproject.repository.mongoRepo.WatchListRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WatchListService {
    private final WatchListRepository watchListRepository;

    @Transactional
    public void save(String email){
        watchListRepository.save(
                WatchList.builder()
                        .email(email)
                        .build()
        );
    }

    public WatchList findByEmail(String email){
        return watchListRepository.findWatchListByEmail(email);
    }

    @Transactional
    public void addBookmark(OTT ott, String email){
        WatchList watchList = watchListRepository.findWatchListByEmail(email);

        watchList.addBookmark(ott);
        watchListRepository.save(watchList);
    }

    @Transactional
    public void deleteBookmark(OTT ott, String email){
        WatchList watchList = watchListRepository.findWatchListByEmail(email);

        watchList.deleteBookmark(ott);
        watchListRepository.save(watchList);
    }

    @Transactional
    public void delete(String email){
        WatchList watchList = watchListRepository.findWatchListByEmail(email);

        watchListRepository.delete(watchList);
    }
}
