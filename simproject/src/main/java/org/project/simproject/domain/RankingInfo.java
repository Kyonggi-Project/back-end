package org.project.simproject.domain;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "rankinginfo")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RankingInfo {
    @Id
    private String id;
    private String ott;
    private String category;
    private List<OTT> rankingList = new ArrayList<>();

    @Builder
    public RankingInfo(String ott, String category){
        this.ott = ott;
        this.category = category;
    }

    public void addRankingList(OTT ott){
        this.rankingList.add(ott);
    }

    public void deleteRankingList(){
        this.rankingList.clear();
    }
}
