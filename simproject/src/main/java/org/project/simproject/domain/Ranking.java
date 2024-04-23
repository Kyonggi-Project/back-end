package org.project.simproject.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Ranking {
    private Movie movie;
    private int rank;
}
