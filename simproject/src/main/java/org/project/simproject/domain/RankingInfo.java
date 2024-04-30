package org.project.simproject.domain;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "rankinginfo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RankingInfo {
    @Id
    private String id;
    private String ott;
    private String category;
    private List<OTTContents> rankingList = new ArrayList<>();

    public void addRankingList(OTTContents ott){
        this.rankingList.add(ott);
    }

    public void deleteRankingList(){
        this.rankingList.clear();
    }

    @Builder
    public RankingInfo(String ott, String category, List<OTTContents> rankingList){
        this.ott = ott;
        this.category = category;
        this.rankingList = rankingList;
    }
}
