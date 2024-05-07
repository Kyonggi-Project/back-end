package org.project.simproject.util.comparator;

import org.project.simproject.domain.OTTContents;

import java.util.Comparator;

public class OTTContentsScoreComparator implements Comparator<OTTContents> {
    // OTTContents Score 기준 내림차순 정렬
    public int compare(OTTContents o1, OTTContents o2) {
        return Double.compare(o2.getScore(), o1.getScore());
    }
}
