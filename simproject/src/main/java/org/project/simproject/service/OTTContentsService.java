package org.project.simproject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.simproject.domain.OTTContents;
import org.project.simproject.domain.RankingInfo;
import org.project.simproject.repository.mongoRepo.OTTContentsRepository;
import org.project.simproject.repository.mongoRepo.RankingInfoRepository;
import org.project.simproject.util.comparator.OTTContentsRankingScoreComparator;
import org.project.simproject.util.comparator.OTTContentsRatingComparator;
import org.project.simproject.util.comparator.OTTContentsScoreComparator;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OTTContentsService {

    private final SentimentAnalysisService sentimentAnalysisService;

    private final OTTContentsRepository ottContentsRepository;
    private final RankingInfoRepository rankingInfoRepository;

    private final List<String> GENRES = new ArrayList<>((Arrays.asList("액션",
            "SF",
            "판타지",
            "어드벤처(모험)", "어드벤처", "모험",
            "범죄",
            "스릴러",
            "미스터리",
            "코미디",
            "멜로/로맨스", "멜로", "로맨스",
//            "드라마",
            "애니메이션",
            "공포(호러)", "공포", "호러",
            "예능",
            "다큐멘터리",
            "음악",
            "가족",
            "서부극(웨스턴)", "서부극", "웨스턴",
            "전쟁",
            "공연",
            "성인")));

    private final List<String> CHECK_WORD_LIST = new ArrayList<>(Arrays.asList("영화",
            "드라마",
            "컨텐츠",
            "시리즈",
            "작품"));

    public OTTContents findById(String id) {
        return ottContentsRepository.findById(id).orElse(null);
    }

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

        int i = 0;
        while (ottContentsList.size() < 20) {
            OTTContents ottContents = contentsByGenreList.get(i);
            if (!ottContentsList.contains(ottContents)) {
                ottContentsList.add(ottContents);
            }
            i++;
        }

        return ottContentsList;
    }

    public List<OTTContents> getContentsByEmotion(String emotion) throws IOException {
        // 문장의 Rating 이용해 작품 찾기
        float rating = sentimentAnalysisService.analyzeSentiment(emotion).getScore();

        List<OTTContents> contentsListByRating = ottContentsRepository.findAllByRatingBetween((float) (rating - 0.1), (float) (rating + 0.1));
        if (contentsListByRating.size() < 20) {     // contentsListByRating의 size가 20개가 되지 않는 경우
            float f = 0.15F;
            while (contentsListByRating.size() <= 20) {
                contentsListByRating = ottContentsRepository.findAllByRatingBetween(rating - f, rating + f);
                f += 0.05F;
            }
        }
        contentsListByRating.sort(new OTTContentsRatingComparator());

        // 입력된 감정의 rating과 각 콘텐츠의 rating 사이의 차이를 계산하여 가장 작은 차이를 가진 작품 선택
        OTTContents contentsByRating = null;
        double minDifference = Double.MAX_VALUE;
        for (OTTContents content : contentsListByRating) {
            double difference = Math.abs(rating - content.getRating());
            if (difference < minDifference) {   // 격차가 더 적을 경우
                minDifference = difference;
                contentsByRating = content;
            } else if (difference > minDifference) {    // 격차가 더 커지는 경우
                break;
            } else if (difference == minDifference) {   // 격차가 0인 경우
                contentsByRating = content;
                break;
            }
        }

        List<OTTContents> contentsList;
        int i = contentsListByRating.indexOf(contentsByRating);
        if (i < 10) {
            contentsList = contentsListByRating.subList(0, 20);
        } else {
            contentsList = contentsListByRating.subList(i - 10, i + 10);
        }

        return contentsList;
    }

    public List<OTTContents> getContentsByClaim(String claim) throws IOException {
        // claim 문자열 가공
        int index = claim.length();
        for (String checkingWord : CHECK_WORD_LIST) {
            if (claim.contains(checkingWord)) {
                index = claim.indexOf(checkingWord);
                break;
            }
        }
        claim = claim.substring(0, index);

        List<OTTContents> ottContentsList = new ArrayList<>();

        // 문장 속 장르가 존재한다면, 이를 통한 작품 찾기
        List<String> genreList = new ArrayList<>();
        for (String genre : GENRES) {
            if (claim.contains(genre)) {
                genreList.add(genre);
            }
        }

        List<OTTContents> contentsByGenreList = new ArrayList<>();
        int genreSize = genreList.size();
        for (String genre : genreList) {
            List<OTTContents> contentsByGenre = ottContentsRepository.findAllByGenreListContainsIgnoreCase(genre);
            contentsByGenre.sort(new OTTContentsScoreComparator());
            contentsByGenreList.addAll(contentsByGenre.subList(0, 15 / genreSize));
        }

        // 문장의 감정분석 Rating 이용해 작품 찾기
        int sentimentSize = 20 - contentsByGenreList.size();
        List<OTTContents> contentsBySentiment = getContentsByEmotion(claim);
        contentsBySentiment = contentsBySentiment.subList((int) (10 - Math.floor((double) sentimentSize / 2)), (int) (10 + Math.ceil((double) sentimentSize / 2)));
        ottContentsList.addAll(contentsByGenreList);
        ottContentsList.addAll(contentsBySentiment);

        Collections.shuffle(ottContentsList);   // 장르/감정분석 이용 작품 랜덤 배치
        return ottContentsList.subList(0, 20);
    }

    // RakingInfo의 OTTContents들을 RankingSocre 기준으로 정렬한 후, return
    public List<OTTContents> getAllContentsByRankingInfo() {
        List<RankingInfo> rankingInfoList = rankingInfoRepository.findAll();
        List<OTTContents> ottContentsList = new ArrayList<>();
        for (RankingInfo rankingInfo : rankingInfoList) {
            ottContentsList.addAll(rankingInfo.getRankingList());
        }
        ottContentsList.sort(new OTTContentsRankingScoreComparator());
        return ottContentsList.stream().distinct().toList();
    }

    public List<OTTContents> getOTTContentsByTitle(String search) {
        return ottContentsRepository.findAllByTitleContainsIgnoreCaseOrSubtitleListContainsIgnoreCase(search, search);
    }
}
