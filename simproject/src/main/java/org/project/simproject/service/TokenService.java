package org.project.simproject.service;

import lombok.RequiredArgsConstructor;
import org.project.simproject.config.jwt.JwtTokenProvider;
import org.project.simproject.domain.User;
import org.project.simproject.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public String createNewAccessToken(String refreshToken){
        if(!jwtTokenProvider.validateToken(refreshToken)) throw new IllegalArgumentException("Token Not Found");
        else{
            User user = refreshTokenRepository.findByRefreshToken(refreshToken).getUserId();
            return jwtTokenProvider.createToken(user, Duration.ofMinutes(15));
        }
    }
}
