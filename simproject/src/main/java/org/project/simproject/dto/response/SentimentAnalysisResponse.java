package org.project.simproject.dto.response;

import lombok.*;
import org.project.simproject.domain.OTTContents;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SentimentAnalysisResponse {
    List<OTTContents> contentsByEmotion;
    List<OTTContents> contentsByClaim;
}
