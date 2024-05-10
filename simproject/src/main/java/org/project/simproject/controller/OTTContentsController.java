package org.project.simproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.OTTContents;
import org.project.simproject.service.OTTContentsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ottdata")
@Tag(name = "OTT 컨텐츠 시스템", description = "컨텐츠 관련 기능")
public class OTTContentsController {

    private final OTTContentsService ottContentsService;

    @Operation(summary = "작품 상세페이지", description = "작품 상세페이지용 OTT Contents")
    @GetMapping("/{id}")
    public ResponseEntity<OTTContents> getOTTContents(@PathVariable("id") String id) {
        OTTContents ottContents = ottContentsService.findById(id);

        return ResponseEntity.status(HttpStatus.OK).body(ottContents);
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
