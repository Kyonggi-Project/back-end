package org.project.simproject.domain;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "movies")
@Getter
@Setter
public class Movie {
    @Id
    private String id;
    private String title;
    private int year;
    private String synopsis;
    private String posterImg;
    private String backgroundImg;
    private List<String> genreList = new ArrayList<>();
    private Map<String, String> metadata = new HashMap<>();
    private Map<String, String> actorList = new HashMap<>();
    private Map<String, String> staffList = new HashMap<>();
    private List<String> ottList = new ArrayList<>();
    private double score;
    private int reviewCount;
    private int rating;

    public void addOtt(String ott) {
        if (!ottList.contains(ott)) {
            ottList.add(ott);
        }
    }
}
