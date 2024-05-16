package org.project.simproject.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.simproject.domain.OTTContents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
@NoArgsConstructor
public class OTTContentsResponse {
    private String id;
    private String title;
    private int year;
    private String posterImg;
    private String backgroundImg;
    private List<String> genreList = new ArrayList<>();
    private HashMap<String, String> metaData = new HashMap<>();
    private String synopsis;
    private HashMap<String, String> actorList = new HashMap<>();
    private HashMap<String, String> staffList = new HashMap<>();
    private List<String> ottList = new ArrayList<>();
    private List<String> tagList = new ArrayList<>();
    private double score;
    private int reviewCount;
    private boolean isBookmarked;
    private boolean existOTTReview;

    public OTTContentsResponse(OTTContents ottContents, boolean isBookmarked, boolean existOTTReview){
        this.id = ottContents.getId();
        this.title = ottContents.getTitle();
        this.year = ottContents.getYear();
        this.posterImg = ottContents.getPosterImg();
        this.backgroundImg = ottContents.getBackgroundImg();
        this.genreList = ottContents.getGenreList();
        this.metaData = ottContents.getMetaData();
        this.synopsis = ottContents.getSynopsis();
        this.actorList = ottContents.getActorList();
        this.staffList = ottContents.getStaffList();
        this.ottList = ottContents.getOttList();
        this.tagList = ottContents.getTagList();
        this.score = ottContents.getScore();
        this.reviewCount = ottContents.getReviewCount();
        this.isBookmarked = isBookmarked;
        this.existOTTReview = existOTTReview;
    }
}
