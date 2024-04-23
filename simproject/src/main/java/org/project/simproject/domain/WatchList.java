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
    private List<OTTContents> bookmark = new ArrayList<>();

    public void addBookmark(OTTContents ott){
        this.bookmark.add(ott);
    }

    public void deleteBookmark(OTTContents ott){
        this.bookmark.remove(ott);
    }

    @Builder
    public WatchList(String email){
        this.email = email;
    }
}
