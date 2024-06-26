package org.project.simproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.Article;
import org.project.simproject.domain.User;
import org.project.simproject.dto.request.AddArticleRequest;
import org.project.simproject.dto.response.ArticleResponse;
import org.project.simproject.dto.request.ModifyArticleRequest;
import org.project.simproject.service.ArticleLikeService;
import org.project.simproject.service.ArticleService;
import org.project.simproject.service.BookmarkService;
import org.project.simproject.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/article")
@Tag(name = "게시글 시스템", description = "게시글 관련 기능")
public class ArticleController {
    private final ArticleService articleService;
    private final BookmarkService bookmarkService;
    private final ArticleLikeService articleLikeService;
    private final UserService userService;

    @Operation(summary = "게시글 추가하기", description = "게시글 서비스에서 데이터베이스에 게시글 데이터 추가")
    @PostMapping("/addArticle")
    public ResponseEntity<Article> addArticle(@RequestBody AddArticleRequest request,
                                              @RequestParam Long id){
        User user = userService.findById(id);
        Article article = articleService.save(request, user);

        return ResponseEntity.status(HttpStatus.CREATED).body(article);
    }

    @Operation(summary = "게시글 모두 보기", description = "게시글 서비스에서 모든 게시글 불러오기")
    @GetMapping("/allArticles")
    public ResponseEntity<Page<ArticleResponse>> getAllArticles(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable
            ){
        Page<ArticleResponse> articleList = articleService.findAll(pageable);

        return ResponseEntity.status(HttpStatus.OK).body(articleList);
    }

    @Operation(summary = "특정 게시글 보기", description = "게시글 서비스에서 특정 게시글 불러오기")
    @GetMapping("/viewArticle/{id}")
    public ResponseEntity<ArticleResponse> getArticle(@PathVariable Long id){
        ArticleResponse response = new ArticleResponse(articleService.findById(id));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "특정 유저의 게시글 모두 보기", description = "게시글 서비스에서 list로 받아옴")
    @GetMapping("/viewArticle/author")
    public ResponseEntity<Page<ArticleResponse>> getArticleByAuthor(@RequestParam String author,
                                                                    @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable){
        Page<ArticleResponse> list = articleService.findByAuthor(author, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @Operation(summary = "특정 제목 및 내용을 포함한 게시글 모두 보기", description = "게시글 서비스에서 list로 받아옴")
    @GetMapping("/viewArticles/content")
    public ResponseEntity<Page<ArticleResponse>> getArticleByContent(@RequestParam String content,
                                                                     @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable){
        Page<ArticleResponse> list = articleService.findByContent(content, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @Operation(summary = "북마크 게시글 모두 보기", description = "북마크 서비스에서 DTO 가져오기")
    @GetMapping("/bookmarked")
    public ResponseEntity<Page<ArticleResponse>> getBookmarkedArticles(@RequestParam Long userId,
                                                                       @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable){
        Page<ArticleResponse> list = bookmarkService.findBookmarkedArticlesByUser(userId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @Operation(summary = "좋아요 게시글 모두 보기", description = "ArticleLike 서비스에서 DTO 가져오기")
    @GetMapping("/liked")
    public ResponseEntity<Page<ArticleResponse>> getLikedArticles(@RequestParam Long userId,
                                                                  @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable){
        Page<ArticleResponse> list = articleLikeService.findLikedArticlesByUser(userId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @Operation(summary = "게시글 수정하기", description = "ModifyRequest 가져오기")
    @PutMapping("/update/{id}")
    public ResponseEntity<Article> modifyArticle(@RequestBody ModifyArticleRequest request,
                                          @PathVariable Long id){
        Article article = articleService.modify(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(article);
    }

    @Operation(summary = "게시글 삭제하기", description = "특정 게시글 데이터 DB에서 삭제하기")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id, @RequestParam Long userId){
        articleService.delete(id, userService.findById(userId));
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
