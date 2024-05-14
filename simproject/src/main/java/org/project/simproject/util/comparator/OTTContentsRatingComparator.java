package org.project.simproject.util.comparator;

import org.project.simproject.domain.OTTContents;

import java.util.Comparator;

public class OTTContentsRatingComparator implements Comparator<OTTContents> {
    // OTTContents Rating 기준 내림차순 정렬
    public int compare(OTTContents o1, OTTContents o2) {
        return Float.compare(o2.getRating(), o1.getRating());
    }
}
