package org.project.simproject.controller;

import lombok.RequiredArgsConstructor;
import org.project.simproject.dto.response.AccessTokenResponse;
import org.project.simproject.dto.request.RefreshTokenRequest;
import org.project.simproject.service.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token")
public class TokenController {
    private final TokenService tokenService;

    @PostMapping("/createToken")
    public ResponseEntity<AccessTokenResponse> createNewAccessToken(@RequestBody RefreshTokenRequest request){
        String newToken = tokenService.createNewAccessToken(request.getRefreshToken());
        return ResponseEntity.status(HttpStatus.OK).body(new AccessTokenResponse(newToken));
    }
}
