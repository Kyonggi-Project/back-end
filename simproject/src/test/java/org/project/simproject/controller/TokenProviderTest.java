package org.project.simproject.controller;

import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.project.simproject.config.jwt.JwtFactory;
import org.project.simproject.config.jwt.JwtTokenProvider;
import org.project.simproject.config.jwt.Properties;
import org.project.simproject.domain.User;
import org.project.simproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TokenProviderTest {
    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Properties jwtProperties;

    @DisplayName("토큰 생성 테스트")
    @Test
    public void test1(){
        User user = userRepository.save(User.builder()
                .email("user@gmail.com")
                .password("test")
                .build());

        String token = tokenProvider.createToken(user, Duration.ofDays(14));

        Long id = Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody()
                .get("id", Long.class);
        assertThat(id).isEqualTo(user.getId());
    }

    @DisplayName("토큰 만료 테스트")
    @Test
    public void test2(){
        JwtFactory jwtFactory = JwtFactory.builder()
                .expiration(new Date(new Date().getTime() - Duration.ofDays(7).toMillis()))
                .build();

        String token = jwtFactory.createToken(jwtProperties);

        boolean valid = tokenProvider.validateToken(token);

        assertThat(valid).isFalse();
    }

    @DisplayName("토큰 유효 테스트")
    @Test
    public void test3(){
        String token = JwtFactory.withDefaultValues().createToken(jwtProperties);
        boolean valid = tokenProvider.validateToken(token);
        assertThat(valid).isTrue();
    }

    @DisplayName("토큰을 통해 인증 객체 생성")
    @Test
    public void test4(){
        String email = "user@gmail.com";
        JwtFactory factory = JwtFactory.builder()
                .claims(Map.of("email", email))
                .build();
        String token = factory.createToken(jwtProperties);

        Authentication authentication = tokenProvider.getAuthentication(token);

        assertThat(((UserDetails)authentication.getPrincipal()).getUsername()).isEqualTo(email);
    }

    @DisplayName("토큰을 통해 유저 ID 가져오기")
    @Test
    public void test5(){
        Long id = 1L;
        String token = JwtFactory.builder()
                .claims(Map.of("id", id))
                .build()
                .createToken(jwtProperties);

        Long claim = tokenProvider.getId(token);

        assertThat(claim).isEqualTo(id);
    }
}