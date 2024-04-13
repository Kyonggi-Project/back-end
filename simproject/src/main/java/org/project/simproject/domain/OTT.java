package org.project.simproject.domain;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Document(collection = "ottdata")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OTT {
    @Id
    private String id;
    private String title;
    private int year;
    private String posterImg;
    private String backgroundImg;
    private List<String> tagList = new ArrayList<>();
    private HashMap<String, String> metaData = new HashMap<>();
    private String synopsis;
    private HashMap<String, String> actorList = new HashMap<>();
    private HashMap<String, String> staffList = new HashMap<>();
    private List<String> ottList = new ArrayList<>();
    private double score;
    private int reviewCount;
    private float rating;

    public void addOTTList(String ott){
        this.ottList.add(ott);
    }

    public void modifyScore(int reviewCount, double score){
        this.reviewCount = reviewCount;
        this.score = score / reviewCount;
    }

    @Builder
    public OTT(String title, int year, String posterImg, String backgroundImg, List<String> tagList, HashMap<String, String> metaData,
               String synopsis, HashMap<String, String> actorList, HashMap<String, String> staffList, double score, int reviewCount,
               float rating){
        this.title = title;
        this.year = year;
        this.posterImg = posterImg;
        this.backgroundImg = backgroundImg;
        this.tagList = tagList;
        this.metaData = metaData;
        this.synopsis = synopsis;
        this.actorList = actorList;
        this.staffList = staffList;
        this.score = score;
        this.reviewCount = reviewCount;
        this.rating = rating;
    }

}
