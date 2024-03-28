package org.project.simproject.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "ranking_info")
@Getter
@Setter
public class RankingInfo {
    @Id
    private String id;
    private String ott;
    private String category;
    private LocalDate date;
    private List<Ranking> rankings;
}

