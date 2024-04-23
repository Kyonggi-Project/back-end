package org.project.simproject.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.language.v1.*;
import org.project.simproject.dto.response.SentimentResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;

@Service
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@PropertySource("classpath:application-sentiment.properties")
public class SentimentAnalysisService {
    @Value("${GOOGLE.SENTIMENT.KEY_PATH}")
    private String keyPath;

    public SentimentResponseDTO analyzeSentiment(String myString) throws IOException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(keyPath));

        try (LanguageServiceClient language = LanguageServiceClient.create(LanguageServiceSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build())) {

            Document doc = Document.newBuilder()
                    .setContent(myString)
                    .setType(Document.Type.PLAIN_TEXT)
                    .build();

            AnalyzeSentimentResponse response = language.analyzeSentiment(doc);

            Sentiment sentiment = response.getDocumentSentiment();

            SentimentResponseDTO responseDTO = new SentimentResponseDTO();
            responseDTO.setMagnitude(sentiment.getMagnitude());
            responseDTO.setScore(sentiment.getScore());
            return responseDTO;

        } catch (IOException e) {
            throw new IOException("Error analyzing sentiment", e);
        }
    }
}