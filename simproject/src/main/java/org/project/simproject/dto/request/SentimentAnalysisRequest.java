package org.project.simproject.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SentimentAnalysisRequest {
    String emotion;
    String claim;
}
