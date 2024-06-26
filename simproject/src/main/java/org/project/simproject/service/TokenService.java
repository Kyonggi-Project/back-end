package org.project.simproject.service;

import lombok.RequiredArgsConstructor;
import org.project.simproject.config.jwt.JwtTokenProvider;
import org.project.simproject.domain.User;
import org.project.simproject.repository.entityRepo.RefreshTokenRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtTokenProvider jwtTokenProvider;
    
    private final RefreshTokenRepository refreshTokenRepository;

    public String createNewAccessToken(String refreshToken){
        User user = findByUserId(refreshToken);
        return jwtTokenProvider.createToken(user, Duration.ofMinutes(15));
    }

    public User findByUserId(String refreshToken){
        if(!jwtTokenProvider.validateToken(refreshToken)) throw new IllegalArgumentException("Token Not Found");
        else{
            return refreshTokenRepository.findByRefreshToken(refreshToken).getUserId();
        }
    }
}
