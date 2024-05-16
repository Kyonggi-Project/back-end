package org.project.simproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.OTTContents;
import org.project.simproject.dto.request.SentimentAnalysisRequest;
import org.project.simproject.dto.response.OTTContentsResponse;
import org.project.simproject.dto.response.SentimentAnalysisResponse;
import org.project.simproject.service.OTTContentsService;
import org.project.simproject.service.OTTReviewService;
import org.project.simproject.service.WatchListService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ottdata")
@Tag(name = "OTT 컨텐츠 시스템", description = "컨텐츠 관련 기능")
public class OTTContentsController {

    private final OTTContentsService ottContentsService;

    private final WatchListService watchListService;

    private final OTTReviewService ottReviewService;

    @Operation(summary = "작품 상세페이지(비로그인)", description = "작품 상세페이지용 OTT Contents(Not Login version)")
    @GetMapping("/{id}")
    public ResponseEntity<OTTContents> getOTTContents(@PathVariable String id) {
        OTTContents ottContents = ottContentsService.findById(id);

        return ResponseEntity.status(HttpStatus.OK).body(ottContents);
    }

    @Operation(summary = "작품 상세페이지(로그인)", description = "작품 상세페이지용 OTT Contents(Login version)")
    @GetMapping("/authorize/{id}")
    public ResponseEntity<OTTContentsResponse> getOTTContentsForLogin(@PathVariable String id, Principal principal) throws UserPrincipalNotFoundException {
        try {
            OTTContents ott = ottContentsService.findById(id);
            boolean isBookmarked = watchListService.isBookmarked(ott, principal.getName());
            boolean existOTTReview = ottReviewService.existsOTTReview(id, principal.getName());

            return ResponseEntity.status(HttpStatus.OK).body(new OTTContentsResponse(ott, isBookmarked, existOTTReview));
        } catch (Exception e){
            throw new UserPrincipalNotFoundException("인증 실패");
        }
    }

    @Operation(summary = "감정분석 이용 작품 추천", description = "메인페이지 모달창을 이용해 입력받은 문장을 통한 작품 추천")
    @GetMapping("/sentiment")
    public ResponseEntity<SentimentAnalysisResponse> getContentsListBySentimentAnalysis(@RequestBody SentimentAnalysisRequest request) throws IOException {
        List<OTTContents> contentsByEmotion = ottContentsService.getContentsByEmotion(request.getEmotion());
        List<OTTContents> contentsByClaim = ottContentsService.getContentsByClaim(request.getClaim());
        SentimentAnalysisResponse response = SentimentAnalysisResponse.builder()
                .contentsByEmotion(contentsByEmotion)
                .contentsByClaim(contentsByClaim)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "인기 TOP 10", description = "서비스 자체 인기 순위 TOP 10")
    @GetMapping("/top10")
    public ResponseEntity<List<OTTContents>> getTop10Contents() {
        List<OTTContents> top10ContentsList = ottContentsService.getTop10Contents();

        return ResponseEntity.status(HttpStatus.OK).body(top10ContentsList);
    }

    @Operation(summary = "메인페이지용 장르별 컨텐츠", description = "메인페이지용 장르별 컨텐츠 20개")
    @GetMapping("/genre")
    public ResponseEntity<List<OTTContents>> getGenreContentsToMain(@RequestParam String genre) {
        List<OTTContents> contentsByGenreList = ottContentsService.get20ContentsByGenre(genre);

        return ResponseEntity.status(HttpStatus.OK).body(contentsByGenreList);
    }

    @Operation(summary = "작품 검색", description = "제목을 이용해 작품 검색")
    @GetMapping("/search")
    public ResponseEntity<List<OTTContents>> getOTTContentsByTitle(@RequestParam String search) {
        List<OTTContents> contentsByGenreList = ottContentsService.getOTTContentsByTitle(search);

        return ResponseEntity.status(HttpStatus.OK).body(contentsByGenreList);
    }

}
