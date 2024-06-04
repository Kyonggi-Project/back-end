package org.project.simproject.config.jwt;

import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final Properties properties;
    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer ";

    public String createToken(User user, Duration tokenValidTerm) {
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(user.getNickname())
                .setIssuer(properties.getIssuer())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidTerm.toMillis()))
                .claim("id", user.getId())
                .claim("email", user.getEmail())
                .signWith(SignatureAlgorithm.HS256, properties.getSecretKey())
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

        return new UsernamePasswordAuthenticationToken(new org.springframework  //인증 객체 토큰 생성 및 가져오기
                .security.core.userdetails.User(getEmail(token), "", authorities), token, authorities);
    }

    public Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(properties.getSecretKey()).parseClaimsJws(token).getBody();
    }

    public Long getId(String token) {
        return getClaims(token).get("id", Long.class);
    }

    public String getEmail(String token) {
        return getClaims(token).get("email", String.class);
    }

    public String resolveToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
        if(authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            return authorizationHeader.substring(TOKEN_PREFIX.length());
        }

        return null;
    }

    public boolean validateToken(String token) {
        try {
            return !getClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

}
