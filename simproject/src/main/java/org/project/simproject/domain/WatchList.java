package org.project.simproject.domain;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "watchlist")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WatchList {
    @Id
    private String id;
    private String email;
    private List<OTT> bookmark = new ArrayList<>();

    @Builder
    public WatchList(String email){
        this.email = email;
    }

    public void addBookmark(OTT ott){
        this.bookmark.add(ott);
    }

    public void deleteBookmark(OTT ott){
        this.bookmark.remove(ott);
    }
}
