package org.project.simproject.dto.response;

import lombok.*;

@Getter
@Setter
public class SentimentResponseDTO {
    private float magnitude;
    private float score;
}