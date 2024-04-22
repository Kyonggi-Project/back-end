package org.project.simproject.controller;

import lombok.RequiredArgsConstructor;
import org.project.simproject.dto.response.SentimentResponseDTO;
import org.project.simproject.service.SentimentAnalysisService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class SentimentAnalysisController {
    private final SentimentAnalysisService sentimentAnalysisService;

    @PostMapping("/analyze-sentiment")
    public SentimentResponseDTO analyzeSentiment(@RequestBody String text) throws IOException {
        return sentimentAnalysisService.analyzeSentiment(text);
    }
}