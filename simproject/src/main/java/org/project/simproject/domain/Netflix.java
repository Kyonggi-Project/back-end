package org.project.simproject.domain;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "netflix")
public class Netflix {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String title;

    String category;

    Long rank;

    @Builder
    Netflix(String title, String category, Long rank) {
        this.title = title;
        this.category = category;
        this.rank = rank;
    }
}
