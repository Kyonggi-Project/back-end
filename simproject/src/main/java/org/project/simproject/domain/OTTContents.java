package org.project.simproject.domain;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Document(collection = "ottdata")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OTTContents {
    @Id
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
    public OTTContents(String title, int year, String posterImg, String backgroundImg, List<String> genreList, HashMap<String, String> metaData,
                       String synopsis, HashMap<String, String> actorList, HashMap<String, String> staffList, double score, int reviewCount,
                       float rating){
        this.title = title;
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
