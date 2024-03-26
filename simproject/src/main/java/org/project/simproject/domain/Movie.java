package org.project.simproject.domain;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
    private String year;
    private String synopsis;
    private String posterImgUrl;
    private String backgroundImgUrl;
    private List<String> seriesGenres = new ArrayList<>();
    private Map<String, String> actors = new HashMap<>();
    private Map<String, String> staffs = new HashMap<>();
}

