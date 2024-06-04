package org.project.simproject.controller;

import lombok.RequiredArgsConstructor;
import org.project.simproject.dto.response.AccessTokenResponse;
import org.project.simproject.service.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token")
public class TokenController {
    private final TokenService tokenService;

    @PostMapping("/createToken")
    public ResponseEntity<AccessTokenResponse> createNewAccessToken(
            @CookieValue(value = "refresh_token", defaultValue = "cookie") String cookie){
        String newToken = tokenService.createNewAccessToken(cookie);
        return ResponseEntity.status(HttpStatus.OK).body(new AccessTokenResponse(newToken));
    }
}
