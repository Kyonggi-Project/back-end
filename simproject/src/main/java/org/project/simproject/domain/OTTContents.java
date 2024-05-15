package org.project.simproject.domain;

import jakarta.persistence.Id;
import lombok.*;
import org.project.simproject.util.comparator.TagMapComparator;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Document(collection = "ottdata")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OTTContents {
    @Id
    private String id;
    private String href;
    private String title;
    private List<String> subtitleList = new ArrayList<>();
    private int year;
    private String posterImg;
    private String backgroundImg;
    private List<String> genreList = new ArrayList<>();
    private HashMap<String, String> metaData = new HashMap<>();
    private String synopsis;
    private LinkedHashMap<String, String> actorList = new LinkedHashMap<>();
    private LinkedHashMap<String, String> staffList = new LinkedHashMap<>();
    private List<String> ottList = new ArrayList<>();
    private HashMap<String, Integer> tagMap = new HashMap<>();
    private List<String> tagList = new ArrayList<>();
    private double score;
    private int reviewCount;
    private float rating;
    private int rankingScore;

    public void addOTTList(String ott){
        if(!this.ottList.contains(ott)){
            this.ottList.add(ott);
        }
    }

    public void modifyScore(int reviewCount, double score){
        this.reviewCount = reviewCount;
        this.score = score;
    }

    public void modifyRating(float score){
        int reviewCount = this.reviewCount;
        float rating = this.rating;

        rating*=(reviewCount-1);
        rating+=score;
        rating/=reviewCount;

        this.rating = rating;
    }

    public void modifyTag(List<String> tags){
        for(String tag : tags){
            if(!this.tagMap.containsKey(tag)){
                this.tagMap.put(tag, 1);
            }
            else {
                int val = this.tagMap.get(tag);
                this.tagMap.put(tag, (val+1));
            }
        }

        this.tagList = TagMapComparator.comparator(this.tagMap);
    }


    public void updateRakingScore(int rankingScore) {
        this.rankingScore += rankingScore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || (getClass() != o.getClass())) return false;
        OTTContents ottContents = (OTTContents) o;
        return Objects.equals(id, ottContents.id);
    }

    @Override
    public int hashCode() {
            return Objects.hash(id);
    }

    @Builder
    public OTTContents(String href, String title, List<String> subtitleList, int year, String posterImg, String backgroundImg, List<String> genreList, HashMap<String, String> metaData,
                       String synopsis, LinkedHashMap<String, String> actorList, LinkedHashMap<String, String> staffList, double score, int reviewCount,
                       float rating){
        this.href = href;
        this.title = title;
        this.subtitleList = subtitleList;
        this.year = year;
        this.posterImg = posterImg;
        this.backgroundImg = backgroundImg;
        this.genreList = genreList;
        this.metaData = metaData;
        this.synopsis = synopsis;
        this.actorList = actorList;
        this.staffList = staffList;
        this.score = score;
        this.reviewCount = reviewCount;
        this.rating = rating;
        this.rankingScore = 0;
    }

}
