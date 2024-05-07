package org.project.simproject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.simproject.domain.OTTContents;
import org.project.simproject.domain.RankingInfo;
import org.project.simproject.repository.mongoRepo.OTTContentsRepository;
import org.project.simproject.repository.mongoRepo.RankingInfoRepository;
import org.project.simproject.util.comparator.OTTContentsRankingScoreComparator;
import org.project.simproject.util.comparator.OTTContentsScoreComparator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OTTContentsService {

    private final OTTContentsRepository ottContentsRepository;
    private final RankingInfoRepository rankingInfoRepository;

    // 인기순위 TOP 10
    public List<OTTContents> getTop10Contents() {
        return getAllContentsByRankingInfo().subList(0, 10);
    }

    // 장르별 컨텐츠 20개
    public List<OTTContents> get20ContentsByGenre(String genre) {
        List<OTTContents> rankingContentsList = getAllContentsByRankingInfo();
        List<OTTContents> ottContentsList = new ArrayList<>();

        // Ranking 컨텐츠 중, 해당 장르 컨텐츠 필터링
        for (OTTContents ottContents : rankingContentsList) {
            if (ottContents.getGenreList().contains(genre)) {
                ottContentsList.add(ottContents);
            }
        }

        // 이미 20개를 찾았다면, return
        if (ottContentsList.size() >= 20) {
            return ottContentsList;
        }

        List<OTTContents> contentsByGenreList = ottContentsRepository.findAllByGenreListContainsIgnoreCase(genre);
        contentsByGenreList.sort(new OTTContentsScoreComparator());
        log.info("Sorting contents by genre");

        int i = 0;
        while (ottContentsList.size() <= 20) {
            OTTContents ottContents = contentsByGenreList.get(i);
            if (!ottContentsList.contains(ottContents)) {
                ottContentsList.add(ottContents);
            }
            i++;
        }

        return ottContentsList;
    }

    // RakingInfo의 OTTContents들을 RankingSocre 기준으로 정렬한 후, return
    public List<OTTContents> getAllContentsByRankingInfo() {
        List<RankingInfo> rankingInfoList = rankingInfoRepository.findAll();
        List<OTTContents> ottContentsList = new ArrayList<>();
        for (RankingInfo rankingInfo : rankingInfoList) {
            ottContentsList.addAll(rankingInfo.getRankingList());
        }
        ottContentsList.sort(new OTTContentsRankingScoreComparator());
        log.info("Get All Contents By Ranking Info");
        return ottContentsList.stream().distinct().toList();
    }

}
