package org.project.simproject.util.comparator;

import org.project.simproject.domain.OTTContents;

import java.util.Comparator;

public class OTTContentsRankingScoreComparator implements Comparator<OTTContents> {
    // OTTContents RankingScore 기준 내림차순 정렬
    public int compare(OTTContents o1, OTTContents o2) {
        return Integer.compare(o2.getRankingScore(), o1.getRankingScore());
    }
}
